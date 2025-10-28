package com.luisd.hodler.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.luisd.hodler.data.local.entity.HoldingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HoldingDao {
    @Query("SELECT * FROM holdings ORDER BY purchaseDate DESC")
    fun getAllHoldings(): Flow<List<HoldingEntity>>

    @Query("SELECT * FROM holdings WHERE id = :id")
    suspend fun getHoldingById(id: Long): HoldingEntity?

    @Query("SELECT * FROM holdings WHERE coinId = :coinId ORDER BY purchaseDate DESC")
    fun getHoldingsByCoinId(coinId: String): Flow<List<HoldingEntity>>

    @Insert(onConflict = REPLACE)
    suspend fun insertHolding(holdingEntity: HoldingEntity): Long

    @Query("DELETE FROM holdings WHERE id = :id")
    suspend fun deleteHoldingById(id: Long): Int

    @Query("DELETE FROM holdings")
    suspend fun deleteAllHoldings(): Int

    @Update
    suspend fun updateHolding(holding: HoldingEntity)
}
