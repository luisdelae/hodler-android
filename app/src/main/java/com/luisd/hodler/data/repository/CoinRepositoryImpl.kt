package com.luisd.hodler.data.repository

import com.luisd.hodler.data.local.dao.CachedCoinDao
import com.luisd.hodler.data.local.dao.CachedCoinDetailDao
import com.luisd.hodler.data.mapper.toCachedEntity
import com.luisd.hodler.data.mapper.toDomain
import com.luisd.hodler.data.mapper.toMarketChart
import com.luisd.hodler.data.remote.api.CoinGeckoApi
import com.luisd.hodler.domain.model.Coin
import com.luisd.hodler.domain.model.CoinDetail
import com.luisd.hodler.domain.model.MarketChart
import com.luisd.hodler.domain.model.PriceData
import com.luisd.hodler.domain.model.Result
import com.luisd.hodler.domain.repository.CoinRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Implementation of CoinRepository with offline-first caching strategy
 *
 * Strategy:
 * 1. Check cache for existing data
 * 2. Attempt to fetch fresh data from API
 * 3. On success: Update cache and return fresh data with isFromCache=false
 * 4. On failure: Return cached data with isFromCache=true if available, otherwise error
 */
class CoinRepositoryImpl @Inject constructor(
    private val api: CoinGeckoApi,
    private val cachedCoinDao: CachedCoinDao,
    private val cachedCoinDetailDao: CachedCoinDetailDao
) : CoinRepository {
    /**
     * Get market coins with offline-first strategy
     */
    override suspend fun getMarketCoins(): Result<List<Coin>> {
        val cached = cachedCoinDao.getAllCachedCoins().first()
        val cacheTimestamp = cachedCoinDao.getLastUpdateTime()

        return try {
            val apiData = api.getMarketCoins()
            cachedCoinDao.insertAllCoins(apiData.map { it.toCachedEntity() })

            Result.Success(
                data = apiData.map { it.toDomain() },
                isFromCache = false,
                lastUpdated = System.currentTimeMillis()
            )
        } catch (e: Exception) {
            if (cached.isNotEmpty()) {
                Result.Success(
                    data = cached.map { it.toDomain() },
                    isFromCache = true,
                    lastUpdated = cacheTimestamp
                )
            } else {
                Result.Error(e)
            }
        }
    }

    /**
     * Get coin details with offline-first strategy
     */
    override suspend fun getCoinDetails(coinId: String): Result<CoinDetail> {
        val cached = cachedCoinDetailDao.getCachedCoinDetail(coinId).first()
        val cacheTimestamp = cachedCoinDetailDao.getLastUpdateTime(coinId)

        return try {
            val apiData = api.getCoinDetails(coinId)
            cachedCoinDetailDao.insertCoinDetail(apiData.toCachedEntity())

            Result.Success(
                data = apiData.toDomain(),
                isFromCache = false,
                lastUpdated = System.currentTimeMillis()
            )
        } catch (e: Exception) {
            if (cached != null) {
                Result.Success(
                    data = cached.toDomain(),
                    isFromCache = true,
                    lastUpdated = cacheTimestamp
                )
            } else {
                Result.Error(e)
            }
        }
    }

    /**
     * Get coin by id with offline-first strategy
     */
    override suspend fun getCoinById(coinId: String): Result<Coin> {
        val cached = cachedCoinDao.getCachedCoinById(coinId).first()
        val cacheTimestamp = cached?.lastUpdated

        return try {
            val response = api.getMarketCoins(coinIds = coinId, perPage = 1)
            val coinDto = response.firstOrNull()

            if (coinDto != null) {
                cachedCoinDao.insertAllCoins(listOf(coinDto.toCachedEntity()))
                Result.Success(
                    data = coinDto.toDomain(),
                    isFromCache = false,
                    lastUpdated = System.currentTimeMillis()
                )
            } else {
                if (cached != null) {
                    Result.Success(
                        data = cached.toDomain(),
                        isFromCache = true,
                        lastUpdated = cacheTimestamp
                    )
                } else {
                    Result.Error(Exception("Coin not found"))
                }
            }
        } catch (e: Exception) {
            if (cached != null) {
                Result.Success(
                    data = cached.toDomain(),
                    isFromCache = true,
                    lastUpdated = cacheTimestamp
                )
            } else {
                Result.Error(e)
            }
        }
    }

    override suspend fun getMarketChart(coinId: String, days: Int): Result<MarketChart> {
        return try {
            val marketChart = api.getCoinMarketChart(coinId = coinId, days = days.toString())
            Result.Success(
                data = marketChart.toMarketChart(),
                isFromCache = false,
                lastUpdated = System.currentTimeMillis()
            )
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Get current prices with offline-first strategy
     */
    override suspend fun getCurrentPrices(coinIds: List<String>): Result<Map<String, PriceData>> {
        return try {
            val idsParam = coinIds.joinToString(",")
            val response = api.getCurrentPrices(coinIds = idsParam)

            val prices = response.mapValues { (_, currencies) ->
                currencies.toDomain()
            }

            Result.Success(
                data = prices,
                isFromCache = false,
                lastUpdated = System.currentTimeMillis()
            )

        } catch (e: Exception) {
            val allCached = cachedCoinDao.getAllCachedCoins().first()
            val cachedPrices = allCached
                .filter { it.id in coinIds }
                .associate { cached ->
                    cached.id to PriceData(
                        usd = cached.currentPrice,
                        usd24hChange = cached.priceChangePercentage24h
                    )
                }

            val cacheTimestamp = cachedCoinDao.getLastUpdateTime()

            if (cachedPrices.isNotEmpty()) {
                Result.Success(
                    data = cachedPrices,
                    isFromCache = true,
                    lastUpdated = cacheTimestamp
                )
            } else {
                Result.Error(e)
            }
        }
    }
}
