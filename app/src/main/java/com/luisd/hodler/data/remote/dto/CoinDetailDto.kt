package com.luisd.hodler.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CoinDetailDto(
    val id: String,
    val symbol: String,
    val name: String,
    val image: ImageDto,
    @property:Json(name = "market_data")
    val marketData: MarketDataDto,
)
