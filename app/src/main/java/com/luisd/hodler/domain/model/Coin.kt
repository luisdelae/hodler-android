package com.luisd.hodler.domain.model

data class Coin(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    val currentPrice: Double,
    val priceChangePercentage24h: Double,
    val marketCap: Long,
    val marketCapRank: Int,
)
