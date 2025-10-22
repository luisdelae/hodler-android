package com.luisd.hodler.data.repository

import com.luisd.hodler.data.local.dao.HoldingDao
import com.luisd.hodler.data.mapper.asResult
import com.luisd.hodler.data.mapper.toDomain
import com.luisd.hodler.data.mapper.toEntity
import com.luisd.hodler.domain.model.Holding
import com.luisd.hodler.domain.model.Result
import com.luisd.hodler.domain.repository.PortfolioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PortfolioRepositoryImpl @Inject constructor(
    val holdingDao: HoldingDao,
) : PortfolioRepository {
    override fun getAllHoldings(): Flow<Result<List<Holding>>> {
        return holdingDao.getAllHoldings()
            .map { entities -> entities.map { it.toDomain() } }
            .asResult()
    }

    override fun getHoldingById(id: Long): Flow<Result<Holding>> {
        return holdingDao.getHoldingById(id = id)
            .map { it.toDomain() }
            .asResult()
    }

    override suspend fun insertHolding(holding: Holding): Result<Unit> {
        return try {
            holdingDao.insertHolding(holding.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteHoldingById(id: Long): Result<Unit> {
        return try {
            holdingDao.deleteHoldingById(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
