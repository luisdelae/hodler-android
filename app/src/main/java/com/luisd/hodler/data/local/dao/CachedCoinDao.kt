package com.luisd.hodler.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.luisd.hodler.data.local.entity.CachedCoinEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for cached coin market data.
 * Provides offline-first access to cryptocurrency market information.
 */
@Dao
interface CachedCoinDao {
    /**
     * Get all cached coins ordered by market cap rank.
     * @return Flow of cached coins for reactive updates
     */
    @Query("SELECT * FROM cached_coins ORDER BY marketCapRank ASC")
    fun getAllCachedCoins(): Flow<List<CachedCoinEntity>>

    /**
     * Get a specific cached coin by ID.
     * @param coinId Unique coin identifier
     * @return Flow of cached coin or null if not found
     */
    @Query("SELECT * FROM cached_coins WHERE id = :coinId")
    fun getCachedCoinById(coinId: String): Flow<CachedCoinEntity?>

    /**
     * Insert or update multiple coins in cache.
     * @param coins List of coins to cache
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCoins(coins: List<CachedCoinEntity>)

    /**
     * Delete cached coins older than specified timestamp.
     * @param timestamp Cutoff time in milliseconds
     */
    @Query("DELETE FROM cached_coins WHERE lastUpdated < :timestamp")
    suspend fun deleteOldCoins(timestamp: Long)

    /**
     * Get timestamp of last cache update.
     * @return Timestamp of most recent cached coin, or null if cache is empty
     */
    @Query("SELECT MAX(lastUpdated) FROM cached_coins")
    suspend fun getLastUpdateTime(): Long?
}
