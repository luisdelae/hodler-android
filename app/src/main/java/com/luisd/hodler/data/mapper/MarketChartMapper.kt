package com.luisd.hodler.data.mapper

import com.luisd.hodler.data.remote.dto.MarketChartDto
import com.luisd.hodler.domain.model.MarketChart
import com.luisd.hodler.domain.model.PricePoint

fun MarketChartDto.toMarketChart(): MarketChart {
    return MarketChart(
        prices =
            prices.map { (timestamp, price) ->
                PricePoint(timestamp = timestamp.toLong(), price = price)
            }
    )
}