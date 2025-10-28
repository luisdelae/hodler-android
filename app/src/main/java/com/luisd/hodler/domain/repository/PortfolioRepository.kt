package com.luisd.hodler.domain.repository

import com.luisd.hodler.domain.model.Holding
import kotlinx.coroutines.flow.Flow
import com.luisd.hodler.domain.model.Result

interface PortfolioRepository {
    fun getAllHoldings(): Flow<Result<List<Holding>>>
    suspend fun getHoldingById(id: Long): Result<Holding>
    suspend fun insertHolding(holding: Holding): Result<Long>
    suspend fun deleteHoldingById(id: Long): Result<Int>
    suspend fun updateHolding(holding: Holding): Result<Unit>
}
