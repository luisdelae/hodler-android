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

/**
 * Implementation of PortfolioRepository for managing user's cryptocurrency holdings.
 *
 * Provides CRUD operations for portfolio holdings stored in local Room database.
 * All holdings are stored locally and do not require network connectivity.
 *
 * @property holdingDao Data access object for holding database operations
 */
class PortfolioRepositoryImpl @Inject constructor(
    val holdingDao: HoldingDao,
) : PortfolioRepository {
    /**
     * Observes all holdings in the portfolio as a reactive stream.
     *
     * Emits a new list whenever holdings are added, updated, or deleted.
     * The Flow automatically updates UI when database changes occur.
     *
     * @return Flow of Result containing list of all holdings, or empty list if no holdings exist
     */
    override fun getAllHoldings(): Flow<Result<List<Holding>>> {
        return holdingDao.getAllHoldings()
            .map { entities -> entities.map { it.toDomain() } }
            .asResult()
    }

    /**
     * Retrieves a specific holding by its unique identifier.
     *
     * Used when editing an existing holding or viewing holding details.
     *
     * @param id Unique identifier of the holding to retrieve
     * @return Result.Success with the holding if found, Result.Error if not found or database error occurs
     */
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

    /**
     * Adds a new holding to the portfolio.
     *
     * Validates and inserts a new cryptocurrency holding with purchase details.
     * The generated ID can be used to reference this holding in future operations.
     *
     * @param holding The holding to add, including coin info, amount, and purchase details
     * @return Result.Success with generated holding ID, or Result.Error if insertion fails
     */
    override suspend fun insertHolding(holding: Holding): Result<Long> {
        return try {
            val result = holdingDao.insertHolding(holding.toEntity())
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Deletes a holding from the portfolio by its unique identifier.
     *
     * Permanently removes the holding from the database. This action cannot be undone.
     *
     * @param id Unique identifier of the holding to delete
     * @return Result.Success with number of rows deleted (0 or 1), or Result.Error if deletion fails
     */
    override suspend fun deleteHoldingById(id: Long): Result<Int> {
        return try {
            val result = holdingDao.deleteHoldingById(id)
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Updates an existing holding with new information.
     *
     * Used to modify purchase amount, price, or date of an existing holding.
     * The holding ID must match an existing record in the database.
     *
     * @param holding The holding with updated information (ID must match existing holding)
     * @return Result.Success if update succeeds, Result.Error if update fails
     */
    override suspend fun updateHolding(holding: Holding): Result<Unit> {
        return try {
            holdingDao.updateHolding(holding.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
