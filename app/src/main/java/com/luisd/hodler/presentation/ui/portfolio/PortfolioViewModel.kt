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

/**
 * ViewModel for the Portfolio screen that manages user's cryptocurrency holdings.
 *
 * Responsibilities:
 * - Observes and transforms holdings with live prices into UI state
 * - Aggregates individual holdings into portfolio-level summary statistics
 * - Groups holdings by coin for organized display
 * - Manages UI state for expandable coin groups
 * - Handles holding deletion
 *
 * The ViewModel combines holdings data from the database with live/cached cryptocurrency prices
 * to provide real-time portfolio valuation and profit/loss calculations.
 *
 * @property observePortfolioUseCase Use case that observes holdings and enriches them with current prices
 * @property portfolioRepository Repository for portfolio CRUD operations
 */
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

    /**
     * UI state flow representing the current portfolio state.
     *
     * Transforms holdings with prices into displayable UI state:
     * - Loading: Initial state while fetching data
     * - Empty: User has no holdings
     * - Success: Holdings grouped by coin with aggregated summary
     * - Error: Failed to load portfolio data
     *
     * The state combines:
     * 1. Holdings data (from use case)
     * 2. Expanded coin IDs (for UI expand/collapse state)
     *
     * State is shared across configuration changes and survives process death
     * with 5-second stop timeout.
     */
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

    /**
     * Triggers a refresh of portfolio prices.
     *
     * Emits a refresh signal that causes the use case to re-fetch current cryptocurrency prices
     * and recalculate all portfolio values. Used for manual refresh (pull-to-refresh).
     */
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

    /**
     * Groups individual holdings by cryptocurrency for organized display.
     *
     * @param holdings List of all individual holdings with prices
     * @return List of CoinGroup, one per unique cryptocurrency held
     */
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

    /**
     * Calculates profit/loss percentage for a group of holdings of the same coin.
     *
     * @param holdingsList List of holdings for a single cryptocurrency
     * @return Profit/loss percentage relative to total cost basis, or 0.0 if cost basis is zero
     */
    private fun calculateGroupPercent(holdingsList: List<HoldingWithPrice>): Double {
        val totalCostBasis = holdingsList.sumOf { it.costBasis }
        val totalCurrentValue = holdingsList.sumOf { it.currentValue }

        return if (totalCostBasis > 0) {
            ((totalCurrentValue - totalCostBasis) / totalCostBasis) * 100
        } else {
            0.0
        }
    }

    /**
     * Deletes a holding from the portfolio.
     *
     * Removes the holding from the database. The portfolio UI automatically updates
     * via the reactive Flow when the deletion completes.
     *
     * @param id Unique identifier of the holding to delete
     */
    fun deleteHolding(id: Long) {
        viewModelScope.launch {
            portfolioRepository.deleteHoldingById(id)
        }
    }

    /**
     * Toggles the expansion state of a coin group in the UI.
     *
     * When expanded, individual holdings within the group are visible.
     * When collapsed, only the aggregated group summary is shown.
     *
     * @param coinId Unique identifier of the coin to expand/collapse
     */
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

/**
 * Represents a group of holdings for a single cryptocurrency.
 *
 * Used to display aggregated information when a user has multiple purchases
 * of the same coin. Shows combined totals while maintaining access to individual
 * holding details.
 *
 * @property coinId Unique identifier for the cryptocurrency
 * @property coinSymbol Trading symbol (e.g., "BTC", "ETH")
 * @property coinName Full name of the cryptocurrency
 * @property imageUrl URL to coin logo/icon
 * @property totalAmount Total quantity held across all purchases
 * @property averageCostBasis Weighted average purchase price per unit
 * @property totalCurrentValue Combined current value of all holdings at current price
 * @property totalProfitLoss Total profit/loss in USD (current value - total cost)
 * @property totalProfitLossPercent Total profit/loss as percentage of cost basis
 * @property holdings List of individual holdings for this coin
 * @property holdingCount Number of separate purchases (derived from holdings.size)
 */
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
