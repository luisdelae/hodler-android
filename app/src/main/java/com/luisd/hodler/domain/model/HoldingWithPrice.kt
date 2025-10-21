package com.luisd.hodler.domain.model

data class HoldingWithPrice(
    val holding: Holding,
    val currentPrice: Double,
    val currentValue: Double,
    val costBasis: Double,
    val profitLoss: Double,
    val profitLossPercent: Double,
    val profitLoss24h: Double,
    val profitLossPercent24h: Double
)
