package com.luisd.hodler.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Database migration from version 1 to 2
 * Adds caching tables for offline-first functionality
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS cached_coins (
                id TEXT PRIMARY KEY NOT NULL,
                symbol TEXT NOT NULL,
                name TEXT NOT NULL,
                image TEXT NOT NULL,
                currentPrice REAL NOT NULL,
                priceChangePercentage24h REAL NOT NULL,
                marketCap INTEGER NOT NULL,
                marketCapRank INTEGER NOT NULL,
                lastUpdated INTEGER NOT NULL
            )
        """.trimIndent())
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS cached_coin_details (
                id TEXT PRIMARY KEY NOT NULL,
                symbol TEXT NOT NULL,
                name TEXT NOT NULL,
                image TEXT NOT NULL,
                currentPrice REAL NOT NULL,
                priceChangePercentage24h REAL NOT NULL,
                marketCapUsd REAL NOT NULL,
                marketCapRank INTEGER NOT NULL,
                totalVolumeUsd REAL NOT NULL,
                circulatingSupply REAL NOT NULL,
                totalSupply REAL,
                maxSupply REAL,
                allTimeHighUsd REAL NOT NULL,
                allTimeHighUsdDate TEXT NOT NULL,
                allTimeLowUsd REAL NOT NULL,
                allTimeLowUsdDate TEXT NOT NULL,
                lastUpdated INTEGER NOT NULL
            )
        """.trimIndent())
    }
}