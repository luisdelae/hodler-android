package com.luisd.hodler.domain.repository

import com.luisd.hodler.domain.model.Coin
import com.luisd.hodler.domain.model.CoinDetail
import com.luisd.hodler.domain.model.MarketChart
import kotlinx.coroutines.flow.Flow
import com.luisd.hodler.domain.model.Result

interface CoinRepository {
    fun getMarketCoins(): Flow<Result<List<Coin>>>
    fun getCoinDetails(coinId: String): Flow<Result<CoinDetail>>
    fun getMarketChart(coinId: String, days: Int): Flow<Result<MarketChart>>
    suspend fun getCurrentPrices(coinIds: List<String>): Result<Map<String, Double>>
}