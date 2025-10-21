package com.luisd.hodler.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PriceDataDto(
    @property:Json(name = "usd")
    val usd: Double,
    @property:Json(name = "usd_24h_change")
    val usd24hChange: Double
)
