package com.luisd.hodler.domain.repository

import com.luisd.hodler.domain.model.Coin
import kotlinx.coroutines.flow.Flow
import com.luisd.hodler.domain.model.Result

interface CoinRepository {
    fun getMarketCoins(): Flow<Result<List<Coin>>>
}