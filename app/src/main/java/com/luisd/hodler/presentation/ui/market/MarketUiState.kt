package com.luisd.hodler.presentation.ui.market

import com.luisd.hodler.domain.model.Coin

sealed interface MarketUiState {
    data object Loading : MarketUiState

    data class Success(
        val coins: List<Coin>,
        val searchQuery: String = "",
        val isSearchActive: Boolean = false,
        val isRefreshing: Boolean = false,
        val isFromCache: Boolean = false,
        val lastUpdated: Long? = null,
    ) : MarketUiState {
        val displayedCoins: List<Coin>
            get() = if (searchQuery.isBlank()) {
                coins
            } else {
                coins.filter { coin ->
                    coin.name.contains(searchQuery, ignoreCase = true) ||
                            coin.symbol.contains(searchQuery, ignoreCase = true)
                }
            }
    }

    data class Error(val message: String) : MarketUiState
}
