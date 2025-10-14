package com.luisd.hodler.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MarketDataDto(
    @property:Json(name = "current_price")
    val currentPrice: Map<String, Double>,
    @property:Json(name = "price_change_percentage_24h")
    val priceChangePercentage24h: Double,
    @property:Json(name = "market_cap")
    val marketCap: Map<String, Double>,
    @property:Json(name = "market_cap_rank")
    val marketCapRank: Int,
    @property:Json(name = "total_volume")
    val totalVolume: Map<String, Double>,
    @property:Json(name = "circulating_supply")
    val circulatingSupply: Double,
    @property:Json(name = "total_supply")
    val totalSupply: Double?,
    @property:Json(name = "max_supply")
    val maxSupply: Double?,
    @property:Json(name="ath")
    val allTimeHigh: Map<String, Double>,
    @property:Json(name="ath_date")
    val allTimeHighDate: Map<String, String>,
    @property:Json(name="atl")
    val allTimeLow: Map<String, Double>,
    @property:Json(name="atl_date")
    val allTimeLowDate: Map<String, String>,
)
