package com.luisd.hodler.data.repository

import com.luisd.hodler.data.mapper.toDomain
import com.luisd.hodler.data.remote.api.CoinGeckoApi
import com.luisd.hodler.domain.model.Coin
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
            emit(Result.Success(coins.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}