package com.luisd.hodler.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.luisd.hodler.data.local.entity.CachedCoinDetailEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for cached coin detail data.
 * Provides offline-first access to detailed cryptocurrency information.
 */
@Dao
interface CachedCoinDetailDao {
    /**
     * Get cached coin details by ID.
     * @param coinId Unique coin identifier
     * @return Flow of cached coin details or null if not found
     */
    @Query("SELECT * FROM cached_coin_details WHERE id = :coinId")
    fun getCachedCoinDetail(coinId: String): Flow<CachedCoinDetailEntity?>

    /**
     * Insert or update coin detail in cache.
     * @param coinDetail Detailed coin information to cache
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinDetail(coinDetail: CachedCoinDetailEntity)

    /**
     * Delete all cached coin details (for manual cache clearing).
     */
    @Query("DELETE FROM cached_coin_details")
    suspend fun deleteAllCoinDetails()

    /**
     * Delete cached coin details older than specified timestamp.
     * @param timestamp Cutoff time in milliseconds
     */
    @Query("DELETE FROM cached_coin_details WHERE lastUpdated < :timestamp")
    suspend fun deleteOldCoinDetails(timestamp: Long)

    /**
     * Check if coin detail is cached and when it was last updated.
     * @param coinId Unique coin identifier
     * @return Timestamp of last update, or null if not cached
     */
    @Query("SELECT lastUpdated FROM cached_coin_details WHERE id = :coinId")
    suspend fun getLastUpdateTime(coinId: String): Long?
}
