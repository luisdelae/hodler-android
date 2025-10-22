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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val observePortfolioUseCase: ObservePortfolioUseCase,
    private val portfolioRepository: PortfolioRepository,
) : ViewModel() {

    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

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
                            holdings = result.data
                        )
                    }
                }
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

    private fun aggregateSummary(holdings: List<HoldingWithPrice>): PortfolioSummary {
        val coinsOwned = holdings.distinctBy { it.holding.coinId }.size
        val totalCurrentValue = holdings.sumOf { it.currentPrice }
        val totalProfitLoss = holdings.sumOf { it.profitLoss }
        val totalProfitLoss24h = holdings.sumOf { it.profitLoss24h }
        val totalCostBasis = holdings.sumOf { it.costBasis }

        val totalProfileLossPercent = if (totalCostBasis > 0.0) {
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
            totalProfitLossPercent = totalProfileLossPercent,
            totalProfitLoss24h = totalProfitLoss24h,
            totalProfitLossPercent24h = totalProfitLossPercent24h
        )
    }

    fun deleteHolding(id: Long) {
        viewModelScope.launch {
            portfolioRepository.deleteHoldingById(id)
        }
    }
}
