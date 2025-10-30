package com.luisd.hodler.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.luisd.hodler.data.local.dao.CachedCoinDao
import com.luisd.hodler.data.local.dao.CachedCoinDetailDao
import com.luisd.hodler.data.local.dao.HoldingDao
import com.luisd.hodler.data.local.entity.CachedCoinDetailEntity
import com.luisd.hodler.data.local.entity.CachedCoinEntity
import com.luisd.hodler.data.local.entity.HoldingEntity

@Database(
    entities = [
        HoldingEntity::class,
        CachedCoinEntity::class,
        CachedCoinDetailEntity::class
    ],
    version = 2, exportSchema = true)
abstract class HodlerDatabase : RoomDatabase() {
    abstract fun holdingDao(): HoldingDao
    abstract fun cachedCoinDao() : CachedCoinDao
    abstract fun cachedCoinDetailDao() : CachedCoinDetailDao
}
