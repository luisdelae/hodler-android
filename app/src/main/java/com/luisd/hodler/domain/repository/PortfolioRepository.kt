package com.luisd.hodler.domain.repository

import com.luisd.hodler.domain.model.Holding
import kotlinx.coroutines.flow.Flow
import com.luisd.hodler.domain.model.Result

interface PortfolioRepository {
    fun getAllHoldings(): Flow<Result<List<Holding>>>

    fun getHoldingById(id: String): Flow<Result<Holding>>
    suspend fun insertHolding(holding: Holding): Result<Unit>
    suspend fun deleteHoldingById(id: String): Result<Unit>
}