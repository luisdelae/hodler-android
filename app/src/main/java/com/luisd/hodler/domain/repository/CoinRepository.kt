package com.luisd.hodler.domain.repository

import com.luisd.hodler.domain.model.Coin
import com.luisd.hodler.domain.model.CoinDetail
import com.luisd.hodler.domain.model.MarketChart
import com.luisd.hodler.domain.model.PriceData
import com.luisd.hodler.domain.model.Result

interface CoinRepository {
    suspend fun getMarketCoins(): Result<List<Coin>>
    suspend fun getCoinDetails(coinId: String): Result<CoinDetail>
    suspend fun getCoinById(coinId: String): Result<Coin>
    suspend fun getMarketChart(coinId: String, days: Int): Result<MarketChart>
    suspend fun getCurrentPrices(coinIds: List<String>): Result<Map<String, PriceData>>
}
