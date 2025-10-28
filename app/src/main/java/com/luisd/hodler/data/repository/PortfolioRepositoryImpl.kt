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

    override suspend fun getHoldingById(id: Long): Result<Holding> {
        return try {
            val entity = holdingDao.getHoldingById(id)
            if (entity != null) {
                Result.Success(entity.toDomain())
            } else {
                Result.Error(Exception("Holding not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun insertHolding(holding: Holding): Result<Long> {
        return try {
            val result = holdingDao.insertHolding(holding.toEntity())
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteHoldingById(id: Long): Result<Int> {
        return try {
            val result = holdingDao.deleteHoldingById(id)
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateHolding(holding: Holding): Result<Unit> {
        return try {
            holdingDao.updateHolding(holding.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
