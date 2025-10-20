package com.luisd.hodler.domain.model

data class HoldingWithPrice(
    val holding: Holding,
    val currentPrice: Double,
    val currentValue: Double,
    val profitLoss: Double,
    val profitLossPercent: Double,
)
