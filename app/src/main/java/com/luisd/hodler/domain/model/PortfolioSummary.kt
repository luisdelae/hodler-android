package com.luisd.hodler.domain.model

data class PortfolioSummary(
    val coinsOwned: Int,
    val totalValue: Double,
    val totalCostBasis: Double,
    val totalProfitLoss: Double,
    val totalProfitLossPercent: Double,
    val totalProfitLoss24h: Double,
    val totalProfitLossPercent24h: Double
)
