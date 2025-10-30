package com.luisd.hodler.presentation.ui.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisd.hodler.domain.model.HoldingWithPrice
import com.luisd.hodler.domain.model.PortfolioSummary
import com.luisd.hodler.domain.model.Result
import com.luisd.hodler.domain.repository.PortfolioRepository
import com.luisd.hodler.domain.usecase.ObservePortfolioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val observePortfolioUseCase: ObservePortfolioUseCase,
    private val portfolioRepository: PortfolioRepository,
) : ViewModel() {

    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)
    private val expandedCoinIds = MutableStateFlow<Set<String>>(emptySet())

    init {
        viewModelScope.launch {
            refreshTrigger.emit(Unit)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<PortfolioUiState> = refreshTrigger
        .flatMapLatest { observePortfolioUseCase() }
        .map { result ->
            when (result) {
                is Result.Error -> PortfolioUiState.Error(
                    message = result.exception.message ?: "Failed to load portfolio"
                )

                Result.Loading -> PortfolioUiState.Loading
                is Result.Success -> {
                    val holdings = result.data
                    if (holdings.isEmpty()) {
                        PortfolioUiState.Empty
                    } else {
                        PortfolioUiState.Success(
                            summary = aggregateSummary(result.data),
                            holdings = combineHoldingsToGroup(result.data),
                            expandedCoinIds = emptySet(),
                            isFromCache = result.isFromCache,
                            lastUpdated = result.lastUpdated,
                        )
                    }
                }
            }
        }
        .combine(expandedCoinIds) { state, expanded ->
            if (state is PortfolioUiState.Success) {
                state.copy(expandedCoinIds = expanded)
            } else {
                state
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PortfolioUiState.Loading
        )

    fun refreshPrices() {
        viewModelScope.launch {
            refreshTrigger.emit(Unit)
        }
    }

    /**
     * Aggregates individual holdings into portfolio summary statistics
     *
     * @param holdings List of all user holdings with current prices
     * @return Portfolio summary with totals and calculated percentages
     */
    private fun aggregateSummary(holdings: List<HoldingWithPrice>): PortfolioSummary {
        val coinsOwned = holdings.distinctBy { it.holding.coinId }.size
        val totalCurrentValue = holdings.sumOf { it.currentValue }
        val totalProfitLoss = holdings.sumOf { it.profitLoss }
        val totalProfitLoss24h = holdings.sumOf { it.profitLoss24h }
        val totalCostBasis = holdings.sumOf { it.costBasis }

        val totalProfitLossPercent = if (totalCostBasis > 0.0) {
            (totalProfitLoss / totalCostBasis) * 100
        } else 0.0

        val portfolioValue24hAgo = totalCurrentValue - totalProfitLoss24h
        val totalProfitLossPercent24h = if (portfolioValue24hAgo > 0) {
            (totalProfitLoss24h / portfolioValue24hAgo) * 100
        } else 0.0

        return PortfolioSummary(
            coinsOwned = coinsOwned,
            totalValue = totalCurrentValue,
            totalCostBasis = totalCostBasis,
            totalProfitLoss = totalProfitLoss,
            totalProfitLossPercent = totalProfitLossPercent,
            totalProfitLoss24h = totalProfitLoss24h,
            totalProfitLossPercent24h = totalProfitLossPercent24h
        )
    }

    private fun combineHoldingsToGroup(holdings: List<HoldingWithPrice>): List<CoinGroup> {
        return holdings
            .groupBy { it.holding.coinId }
            .map { (coinId, holdingsList) ->
                CoinGroup(
                    coinId = coinId,
                    coinSymbol = holdingsList.first().holding.coinSymbol,
                    coinName = holdingsList.first().holding.coinName,
                    imageUrl = holdingsList.first().holding.imageUrl,
                    totalAmount = holdingsList.sumOf { it.holding.amount },
                    averageCostBasis = holdingsList.sumOf {
                        it.holding.amount * it.holding.purchasePrice
                    } / holdingsList.sumOf { it.holding.amount },
                    totalCurrentValue = holdingsList.sumOf { it.currentValue },
                    totalProfitLoss = holdingsList.sumOf { it.profitLoss },
                    totalProfitLossPercent = calculateGroupPercent(holdingsList),
                    holdings = holdingsList.sortedByDescending { it.holding.purchaseDate }
                )
            }
    }

    private fun calculateGroupPercent(holdingsList: List<HoldingWithPrice>): Double {
        val totalCostBasis = holdingsList.sumOf { it.costBasis }
        val totalCurrentValue = holdingsList.sumOf { it.currentValue }

        return if (totalCostBasis > 0) {
            ((totalCurrentValue - totalCostBasis) / totalCostBasis) * 100
        } else {
            0.0
        }
    }

    fun deleteHolding(id: Long) {
        viewModelScope.launch {
            portfolioRepository.deleteHoldingById(id)
        }
    }

    fun toggleCoinExpansion(coinId: String) {
        expandedCoinIds.update { currentExpanded ->
            if (currentExpanded.contains(coinId)) {
                currentExpanded - coinId
            } else {
                currentExpanded + coinId
            }
        }
    }
}

data class CoinGroup(
    val coinId: String,
    val coinSymbol: String,
    val coinName: String,
    val imageUrl: String,

    val totalAmount: Double,
    val averageCostBasis: Double,
    val totalCurrentValue: Double,
    val totalProfitLoss: Double,
    val totalProfitLossPercent: Double,

    val holdings: List<HoldingWithPrice>,
    val holdingCount: Int = holdings.size
)