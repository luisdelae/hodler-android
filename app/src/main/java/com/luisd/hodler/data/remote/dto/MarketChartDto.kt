package com.luisd.hodler.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MarketChartDto(
    val prices: List<List<Double>>,
)
