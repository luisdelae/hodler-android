package com.luisd.hodler.presentation.ui.portfolio

import com.luisd.hodler.domain.model.PortfolioSummary

sealed interface PortfolioUiState {
    data object Loading : PortfolioUiState

    data object Empty : PortfolioUiState

    data class Success(
        val summary: PortfolioSummary,
        val holdings: List<CoinGroup>,
        val isRefreshing: Boolean = false,
        val expandedCoinIds: Set<String> = emptySet(),
        val isFromCache: Boolean = false,
        val lastUpdated: Long? = null,
    ) : PortfolioUiState

    data class Error(
        val message: String,
    ) : PortfolioUiState
}
