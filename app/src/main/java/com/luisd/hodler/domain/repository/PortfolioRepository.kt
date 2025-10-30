package com.luisd.hodler.domain.repository

import com.luisd.hodler.domain.model.Holding
import kotlinx.coroutines.flow.Flow
import com.luisd.hodler.domain.model.Result

/**
 * Repository interface for managing user's cryptocurrency portfolio holdings.
 *
 * Provides CRUD operations for holdings stored in local database.
 * All operations are local-only and do not require network connectivity.
 */
interface PortfolioRepository {
    /**
     * Observes all holdings in the portfolio as a reactive stream.
     *
     * Emits a new list whenever holdings are added, updated, or deleted in the database.
     * The Flow automatically updates observers when database changes occur.
     *
     * @return Flow of Result containing list of all holdings, or empty list if portfolio is empty
     */
    fun getAllHoldings(): Flow<Result<List<Holding>>>
    /**
     * Retrieves a specific holding by its unique identifier.
     *
     * One-time fetch used when editing an existing holding or viewing holding details.
     *
     * @param id Unique database identifier of the holding
     * @return Result.Success with the holding if found, Result.Error if not found or database error occurs
     */
    suspend fun getHoldingById(id: Long): Result<Holding>
    /**
     * Adds a new holding to the portfolio.
     *
     * Inserts a cryptocurrency holding with purchase details into the local database.
     *
     * @param holding The holding to add, including coin ID, amount, purchase price, and purchase date
     * @return Result.Success with generated database ID, or Result.Error if insertion fails
     */
    suspend fun insertHolding(holding: Holding): Result<Long>
    /**
     * Deletes a holding from the portfolio by its identifier.
     *
     * Permanently removes the holding from the database. This action cannot be undone.
     *
     * @param id Unique database identifier of the holding to delete
     * @return Result.Success with number of rows deleted (0 if not found, 1 if deleted),
     *         or Result.Error if deletion fails
     */
    suspend fun deleteHoldingById(id: Long): Result<Int>
    /**
     * Updates an existing holding with new information.
     *
     * @param holding The holding with updated information (ID must match existing holding)
     * @return Result.Success if update succeeds, Result.Error if holding not found or update fails
     */
    suspend fun updateHolding(holding: Holding): Result<Unit>
}
