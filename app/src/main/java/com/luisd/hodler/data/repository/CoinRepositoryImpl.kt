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
 * 1. Immediately emit cached data if available
 * 2. Fetch fresh data from API in background
 * 3. Update cache with fresh data
 * 4. If API fails and cache exists, keep showing cached data
 * 5. Only show error if API fails AND no cache exists
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

        return try {
            val apiData = api.getMarketCoins()
            cachedCoinDao.insertAllCoins(apiData.map { it.toCachedEntity() })
            Result.Success(apiData.map { it.toDomain() })
        } catch (e: Exception) {
            if (cached.isNotEmpty()) {
                Result.Success(cached.map { it.toDomain() })
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

        return try {
            val apiData = api.getCoinDetails(coinId)
            cachedCoinDetailDao.insertCoinDetail(apiData.toCachedEntity())
            Result.Success(apiData.toDomain())
        } catch (e: Exception) {
            if (cached != null) {
                Result.Success(cached.toDomain())
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

        return try {
            val response = api.getMarketCoins(coinIds = coinId, perPage = 1)
            val coinDto = response.firstOrNull()

            if (coinDto != null) {
                cachedCoinDao.insertAllCoins(listOf(coinDto.toCachedEntity()))
                Result.Success(coinDto.toDomain())
            } else {
                if (cached != null) {
                    Result.Success(cached.toDomain())
                } else {
                    Result.Error(Exception("Coin not found"))
                }
            }
        } catch (e: Exception) {
            if (cached != null) {
                Result.Success(cached.toDomain())
            } else {
                Result.Error(e)
            }
        }
    }

    override suspend fun getMarketChart(coinId: String, days: Int): Result<MarketChart> {
        return try {
            val marketChart = api.getCoinMarketChart(coinId = coinId, days = days.toString())
            Result.Success(marketChart.toMarketChart())
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
            Result.Success(prices)
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

            if (cachedPrices.isNotEmpty()) {
                Result.Success(cachedPrices)
            } else {
                Result.Error(e)
            }
        }
    }
}
