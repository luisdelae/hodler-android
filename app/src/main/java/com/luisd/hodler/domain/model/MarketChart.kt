package com.luisd.hodler.domain.model

data class MarketChart(
    val prices: List<PricePoint>,
)

data class PricePoint(
    val timestamp: Long,
    val price: Double,
)
