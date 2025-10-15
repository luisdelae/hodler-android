package com.luisd.hodler.data.repository

import com.luisd.hodler.data.mapper.toDomain
import com.luisd.hodler.data.mapper.toMarketChart
import com.luisd.hodler.data.remote.api.CoinGeckoApi
import com.luisd.hodler.domain.model.Coin
import com.luisd.hodler.domain.model.CoinDetail
import com.luisd.hodler.domain.model.MarketChart
import com.luisd.hodler.domain.model.Result
import com.luisd.hodler.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(
    private val api: CoinGeckoApi,
) : CoinRepository {
    override fun getMarketCoins(): Flow<Result<List<Coin>>> = flow {
        emit(Result.Loading)

        try {
            val coins = api.getMarketCoins()
            emit(value = Result.Success(data = coins.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(value = Result.Error(exception = e))
        }
    }

    override fun getCoinDetails(coinId: String): Flow<Result<CoinDetail>> = flow {
        emit(Result.Loading)

        try {
            val coinDetails = api.getCoinDetails(coinId)
            emit(value = Result.Success(data = coinDetails.toDomain()))
        } catch (e: Exception) {
            emit(value = Result.Error(exception = e))
        }
    }

    override fun getMarketChart(coinId: String): Flow<Result<MarketChart>> = flow {
        emit(Result.Loading)

        try {
            val marketChart = api.getCoinMarketChart(coinId)
            emit(value = Result.Success(data = marketChart.toMarketChart()))
        } catch (e: Exception) {
            emit(value = Result.Error(exception = e))
        }
    }
}