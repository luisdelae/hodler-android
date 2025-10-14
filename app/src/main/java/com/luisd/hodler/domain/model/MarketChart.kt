package com.luisd.hodler.domain.model

data class MarketChart(
    val prices: List<PricePoint>,
)

data class PricePoint(
    val timeStamp: Long,
    val price: Double,
)
