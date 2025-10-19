package com.luisd.hodler.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.luisd.hodler.data.local.entity.HoldingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HoldingDao {
    @Query("SELECT * FROM holdings ORDER BY purchaseDate DESC")
    fun getAllHoldings(): Flow<List<HoldingEntity>>

    @Query("SELECT * FROM holdings WHERE id = :id")
    fun getHoldingById(id: String): Flow<HoldingEntity>

    @Query("SELECT * FROM holdings WHERE coinId = :coinId ORDER BY purchaseDate DESC")
    fun getHoldingsByCoinId(coinId: String): Flow<List<HoldingEntity>>

    @Insert(onConflict = REPLACE)
    fun insertHolding(holdingEntity: HoldingEntity): Long

    @Query("DELETE FROM holdings WHERE id = :id")
    fun deleteHoldingById(id: String): Int

    @Query("DELETE FROM holdings")
    fun deleteAllHoldings(): Int
}
