package com.luisd.hodler.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Cached coin entity for offline-first market data.
 * Stores essential market information with timestamp for cache validation.
 */
@Entity(tableName = "cached_coins")
data class CachedCoinEntity(
    @PrimaryKey val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    val currentPrice: Double,
    val priceChangePercentage24h: Double,
    val marketCap: Long,
    val marketCapRank: Int,
    val lastUpdated: Long = System.currentTimeMillis()
)
