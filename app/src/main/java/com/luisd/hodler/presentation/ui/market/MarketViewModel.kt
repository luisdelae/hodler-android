package com.luisd.hodler.presentation.ui.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisd.hodler.domain.model.Result
import com.luisd.hodler.domain.repository.CoinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Market screen that displays browsable cryptocurrency market data.
 *
 * Responsibilities:
 * - Loads and manages list of top cryptocurrencies by market cap
 * - Handles search functionality for filtering coins by name or symbol
 * - Manages pull-to-refresh state
 * - Provides cache awareness for offline mode indication
 *
 * The ViewModel loads market data on initialization and provides methods for
 * refreshing data and searching through the coin list.
 *
 * @property repository Repository for fetching cryptocurrency market data
 */
@HiltViewModel
class MarketViewModel @Inject constructor(
    private val repository: CoinRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MarketUiState>(MarketUiState.Loading)

    /**
     * UI state flow representing the current market screen state.
     *
     * States:
     * - Loading: Initial data fetch in progress
     * - Success: Coins loaded with optional search filtering
     * - Error: Failed to load coins (network error, API error, etc.)
     *
     * Success state includes cache metadata to display offline indicators when
     * showing stale data.
     */
    val uiState: StateFlow<MarketUiState> = _uiState.asStateFlow()

    init {
        loadCoins()
    }

    /**
     * Loads cryptocurrency market data from the repository.
     *
     * On initial load, displays Loading state.
     * On refresh (pull-to-refresh), shows refreshing indicator while maintaining current data.
     *
     * Preserves search state across refreshes. If user has an active search query,
     * the filtered results are maintained after refresh completes.
     *
     * @param isRefresh True if triggered by pull-to-refresh, false for initial load
     */
    private fun loadCoins(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh && _uiState.value is MarketUiState.Success) {
                _uiState.update { (it as MarketUiState.Success).copy(isRefreshing = true) }
            }

            val result = repository.getMarketCoins()

            _uiState.value = when (result) {
                is Result.Success -> {
                    val currentState = _uiState.value
                    MarketUiState.Success(
                        coins = result.data,
                        searchQuery = (currentState as? MarketUiState.Success)?.searchQuery
                            ?: "",
                        isSearchActive = (currentState as? MarketUiState.Success)?.isSearchActive
                            ?: false,
                        isRefreshing = false,
                        isFromCache = result.isFromCache,
                        lastUpdated = result.lastUpdated,
                    )
                }

                is Result.Error -> MarketUiState.Error(
                    message = result.exception.message ?: "Unknown error",
                )

                Result.Loading -> if (isRefresh) _uiState.value else MarketUiState.Loading
            }
        }
    }

    /**
     * Updates the search query to filter displayed coins.
     *
     * Filtering is performed in the UI state (MarketUiState.Success.displayedCoins)
     * to provide instant feedback without re-fetching data.
     *
     * @param query Search string to filter coins by name or symbol (case-insensitive)
     */
    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            if (state is MarketUiState.Success) {
                state.copy(searchQuery = query)
            } else state
        }
    }

    /**
     * Toggles the search bar active/inactive state.
     *
     * When search is deactivated, clears the search query to show all coins again.
     * When activated, preserves any existing search query.
     *
     * @param active True to show search bar, false to hide it
     */
    fun onSearchActiveChange(active: Boolean) {
        _uiState.update { state ->
            if (state is MarketUiState.Success) {
                state.copy(
                    isSearchActive = active,
                    searchQuery = if (!active) "" else state.searchQuery
                )
            } else state
        }
    }

    /**
     * Triggers a manual refresh of market data (pull-to-refresh).
     *
     * Maintains current UI state (including search query) while fetching fresh data.
     * Shows refreshing indicator during the operation.
     */
    fun refresh() {
        loadCoins(isRefresh = true)
    }
}
