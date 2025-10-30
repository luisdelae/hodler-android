package com.luisd.hodler.presentation.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.luisd.hodler.domain.model.MarketChart
import com.luisd.hodler.domain.model.Result
import com.luisd.hodler.domain.repository.CoinRepository
import com.luisd.hodler.presentation.navigation.Screen
import com.luisd.hodler.presentation.ui.details.CoinDetailUiState.Error
import com.luisd.hodler.presentation.ui.details.CoinDetailUiState.Loading
import com.luisd.hodler.presentation.ui.details.CoinDetailUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Coin Detail screen that displays comprehensive cryptocurrency information.
 *
 * Responsibilities:
 * - Loads and displays detailed coin information (market stats, supply, ATH/ATL)
 * - Manages historical price chart with multiple time ranges
 * - Handles independent loading states for coin details and chart data
 * - Provides cache awareness for offline mode indication
 *
 * The screen shows two independent data sections:
 * 1. Coin details (loaded once on screen open)
 * 2. Price chart (reloaded when user changes time range)
 *
 * Each section has its own loading/error state to allow showing coin details
 * even if chart fails to load, or vice versa.
 *
 * @property repository Repository for fetching coin details and chart data
 * @property savedStateHandle Contains navigation arguments (coinId and coinSymbol)
 */
@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    private val repository: CoinRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val args = savedStateHandle.toRoute<Screen.CoinDetail>()
    private val coinId: String = args.coinId

    /**
     * Coin symbol for display in the top app bar (e.g., "BTC", "ETH").
     * Passed via navigation to avoid waiting for API response.
     */
    val coinSymbol: String = args.coinSymbol

    private val _state = MutableStateFlow<CoinDetailUiState>(Loading)

    /**
     * UI state flow representing the coin detail screen state.
     *
     * States:
     * - Loading: Initial load of coin details
     * - Success: Coin details loaded, with nested chart state (Loading/Success/Error)
     * - Error: Failed to load coin details (chart not attempted)
     *
     * Success state contains independent chart state to allow coin details to display
     * even when chart loading fails or is still in progress.
     */
    val state: StateFlow<CoinDetailUiState> = _state.asStateFlow()

    init {
        loadCoinDetails()
    }

    /**
     * Loads comprehensive coin details from the repository.
     *
     * On successful load, initializes the UI with coin data and triggers
     * initial chart load for the default time range (7 days).
     *
     * Chart loading happens independently - if it fails, coin details still display.
     */
    private fun loadCoinDetails() {
        viewModelScope.launch {
            val result = repository.getCoinDetails(coinId)
            when (result) {
                is Result.Error -> {
                    _state.value = Error(result.exception.message ?: "Unknown error")
                }

                Result.Loading -> _state.value = Loading
                is Result.Success -> {
                    _state.value = Success(
                        coinDetail = result.data,
                        chartState = ChartState.Loading,
                        timeRange = TimeRange.DAY_7,
                        isFromCache = result.isFromCache,
                        lastUpdated = result.lastUpdated,
                    )
                    loadMarketData(TimeRange.DAY_7)
                }
            }
        }
    }

    /**
     * Loads historical price chart data for the specified time range.
     *
     * Updates only the chart section of the UI state, leaving coin details unchanged.
     * This allows independent success/failure states for details and chart.
     *
     * Chart data is never cached - always fetched fresh from API.
     *
     * @param timeRange Time range for chart data (1 day, 7 days, 30 days, etc.)
     */
    private fun loadMarketData(timeRange: TimeRange) {
        viewModelScope.launch {
            val result = repository.getMarketChart(coinId = coinId, timeRange.days)
            _state.value = when (val currentState = _state.value) {
                is Success -> currentState.copy(
                    chartState = when (result) {
                        is Result.Error -> ChartState.Error(
                            result.exception.message ?: "Chart error"
                        )

                        Result.Loading -> ChartState.Loading
                        is Result.Success<*> -> ChartState.Success(result.data as MarketChart)
                    },
                    timeRange = timeRange
                )

                else -> currentState
            }
        }
    }

    /**
     * Updates the chart to display a different time range.
     *
     * Called when user taps a time range chip (1D, 7D, 1M, 1Y, ALL).
     * Reloads chart data while preserving coin details.
     *
     * @param timeRange New time range to display
     */
    fun updateTimeRange(timeRange: TimeRange) {
        loadMarketData(timeRange)
    }

    /**
     * Triggers a manual refresh of coin details and chart.
     *
     * Reloads both coin details and chart data (with current time range).
     * Used for pull-to-refresh or retry after error.
     */
    fun refresh() {
        loadCoinDetails()
    }
}
