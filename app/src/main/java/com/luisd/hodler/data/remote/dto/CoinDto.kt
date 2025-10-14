package com.luisd.hodler.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CoinDto(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    @property:Json(name = "current_price")
    val currentPrice: Double,
    @property:Json(name = "price_change_percentage_24h")
    val priceChangePercentage24h: Double,
    @property:Json(name = "market_cap")
    val marketCap: Long,
    @property:Json(name = "market_cap_rank")
    val marketCapRank: Int,
)
