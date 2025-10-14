package com.luisd.hodler.domain.model

data class CoinDetail(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    val currentPrice: Double,
    val marketCapUsd: Double,
    val marketCapRank: Int,
    val totalVolumeUsd: Double,
    val priceChangePercentage24h: Double,
    val circulatingSupply: Double,
    val totalSupply: Double?,
    val maxSupply: Double?,
    val allTimeHighUsd: Double,
    val allTimeLowUsd: Double,
    val allTimeHighUsdDate: String,
    val allTimeLowUsdDate: String,
)
