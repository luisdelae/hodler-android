package com.luisd.hodler.data.mapper

import com.luisd.hodler.data.remote.dto.MarketChartDto
import com.luisd.hodler.domain.model.MarketChart

fun MarketChartDto.toMarketChart(): MarketChart {
    return MarketChart(
        prices = listOf()
    )
}