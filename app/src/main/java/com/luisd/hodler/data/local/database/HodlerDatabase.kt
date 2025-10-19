package com.luisd.hodler.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.luisd.hodler.data.local.dao.HoldingDao
import com.luisd.hodler.data.local.entity.HoldingEntity

@Database(entities = [HoldingEntity::class], version = 1, exportSchema = true)
abstract class HodlerDatabase : RoomDatabase() {
    abstract fun holdingDao(): HoldingDao
}