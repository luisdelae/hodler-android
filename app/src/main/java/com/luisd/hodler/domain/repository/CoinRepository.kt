package com.luisd.hodler.domain.repository

import com.luisd.hodler.domain.model.Coin
import com.luisd.hodler.domain.model.CoinDetail
import com.luisd.hodler.domain.model.MarketChart
import com.luisd.hodler.domain.model.PriceData
import com.luisd.hodler.domain.model.Result

/**
 * Repository interface for cryptocurrency market data operations.
 *
 * Provides access to cryptocurrency information including market data, detailed stats,
 * historical charts, and current prices.
 */
interface CoinRepository {
    /**
     * Retrieves a list of cryptocurrencies with market data.
     *
     * @return Result.Success with list of coins, or Result.Error if data cannot be retrieved
     */
    suspend fun getMarketCoins(): Result<List<Coin>>
    /**
     * Retrieves detailed information about a specific cryptocurrency.
     *
     * @param coinId Unique identifier for the cryptocurrency (e.g., "bitcoin", "ethereum")
     * @return Result.Success with coin details, or Result.Error if not found or retrieval fails
     */
    suspend fun getCoinDetails(coinId: String): Result<CoinDetail>
    /**
     * Retrieves basic information about a specific cryptocurrency by ID.
     *
     * @param coinId Unique identifier for the cryptocurrency (e.g., "bitcoin", "ethereum")
     * @return Result.Success with coin data, or Result.Error if not found or retrieval fails
     */
    suspend fun getCoinById(coinId: String): Result<Coin>
    /**
     * Retrieves historical price chart data for a cryptocurrency.
     *
     * Returns time-series price data for rendering charts.
     *
     * @param coinId Unique identifier for the cryptocurrency (e.g., "bitcoin", "ethereum")
     * @param days Number of days of historical data to retrieve (e.g., 1, 7, 30, 365)
     * @return Result.Success with market chart data, or Result.Error if retrieval fails
     */
    suspend fun getMarketChart(coinId: String, days: Int): Result<MarketChart>
    /**
     * Retrieves current prices for multiple cryptocurrencies.
     *
     * Batch fetches current USD prices and 24h changes for specified coins.
     *
     * @param coinIds List of cryptocurrency identifiers (e.g., ["bitcoin", "ethereum", "cardano"])
     * @return Result.Success with map of coin ID to price data, or Result.Error if retrieval fails
     */
    suspend fun getCurrentPrices(coinIds: List<String>): Result<Map<String, PriceData>>
}
