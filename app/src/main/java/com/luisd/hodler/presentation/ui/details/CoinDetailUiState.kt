package com.luisd.hodler.presentation.ui.details

import com.luisd.hodler.domain.model.CoinDetail
import com.luisd.hodler.domain.model.MarketChart

sealed class CoinDetailUiState {
    object Loading : CoinDetailUiState()
    data class Error(val message: String) : CoinDetailUiState()
    data class Success(
        val coinDetail: CoinDetail,
        val chartState: ChartState,
        val timeRange: TimeRange
    ) : CoinDetailUiState()
}

sealed class ChartState {
    object Loading : ChartState()
    data class Success(val chart: MarketChart) : ChartState()
    data class Error(val message: String) : ChartState()
}

enum class TimeRange(val days: Int, val label: String) {
    DAY_1(days = 1, label = "24H"),
    DAY_7(days = 7, label = "7D"),
    DAY_30(days = 30, label = "30D"),
    YEAR_1(days = 365, label = "1Y"),
}
