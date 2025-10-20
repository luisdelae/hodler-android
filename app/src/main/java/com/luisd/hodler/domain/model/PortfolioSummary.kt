package com.luisd.hodler.domain.model

data class PortfolioSummary(
    val totalValue: Double,
    val percentChange24h: Double,
    val valueChange24h: Double,
)
