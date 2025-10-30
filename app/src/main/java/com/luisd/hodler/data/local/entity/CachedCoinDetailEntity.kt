package com.luisd.hodler.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Cached coin detail entity for offline-first detail screen.
 * Stores comprehensive coin information including market stats.
 */
@Entity(tableName = "cached_coin_details")
data class CachedCoinDetailEntity(
    @PrimaryKey val id: String,
    val symbol: String,
    val name: String,
    val image: String,

    // Market data fields
    val currentPrice: Double,
    val priceChangePercentage24h: Double,
    val marketCapUsd: Double,
    val marketCapRank: Int,
    val totalVolumeUsd: Double,
    val circulatingSupply: Double,
    val totalSupply: Double?,
    val maxSupply: Double?,
    val allTimeHighUsd: Double,
    val allTimeHighUsdDate: String,
    val allTimeLowUsd: Double,
    val allTimeLowUsdDate: String,

    val lastUpdated: Long = System.currentTimeMillis()
)
