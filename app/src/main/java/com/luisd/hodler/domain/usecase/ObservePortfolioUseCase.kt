package com.luisd.hodler.domain.usecase

import com.luisd.hodler.domain.model.Holding
import com.luisd.hodler.domain.model.HoldingWithPrice
import com.luisd.hodler.domain.model.PriceData
import com.luisd.hodler.domain.model.Result
import com.luisd.hodler.domain.repository.CoinRepository
import com.luisd.hodler.domain.repository.PortfolioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case that observes portfolio holdings and enriches them with current price data.
 *
 * Combines holdings from local database with live/cached cryptocurrency prices to calculate:
 * - Current value of each holding
 * - Total profit/loss (actual vs purchase price)
 * - 24-hour profit/loss (based on price changes)
 *
 * The use case preserves cache metadata from price data, allowing the UI to indicate
 * when prices are stale or from offline cache.
 *
 * @property portfolioRepository Repository for accessing user's cryptocurrency holdings
 * @property coinRepository Repository for fetching current cryptocurrency prices
 */
class ObservePortfolioUseCase @Inject constructor(
    private val portfolioRepository: PortfolioRepository,
    private val coinRepository: CoinRepository,
) {
    /**
     * Observes portfolio holdings with real-time price calculations.
     *
     * Flow emissions:
     * 1. Loading - Initial state while fetching data
     * 2. Success - Holdings enriched with current prices and profit/loss calculations
     * 3. Error - If fetching prices fails and no cache is available
     *
     * The Success result includes cache metadata (isFromCache, lastUpdated) from the
     * price data, allowing the UI to display appropriate indicators when showing
     * stale or offline data.
     *
     * @return Flow of Result containing list of holdings with current price data,
     *         or empty list if user has no holdings
     */
    operator fun invoke(): Flow<Result<List<HoldingWithPrice>>> {
        return portfolioRepository.getAllHoldings()
            .map { holdingResult ->
                when (holdingResult) {
                    is Result.Error -> Result.Error(holdingResult.exception)
                    Result.Loading -> Result.Loading
                    is Result.Success -> {
                        val holdings = holdingResult.data
                        if (holdings.isEmpty()) {
                            return@map Result.Success(
                                data = emptyList(),
                                isFromCache = false,
                                lastUpdated = System.currentTimeMillis()
                            )
                        }

                        val coinIds = holdings.map { it.coinId }.distinct()

                        val pricesResult = coinRepository.getCurrentPrices(coinIds)
                        when (pricesResult) {
                            is Result.Error -> return@map Result.Error(pricesResult.exception)
                            Result.Loading -> return@map Result.Loading
                            is Result.Success -> {
                                val pricesMap = pricesResult.data

                                val holdingsWithPrices = holdings.map { holding ->
                                    calculateHoldingWithPrice(holding, pricesMap)
                                }

                                Result.Success(
                                    data = holdingsWithPrices,
                                    isFromCache = pricesResult.isFromCache,
                                    lastUpdated = pricesResult.lastUpdated
                                )
                            }
                        }
                    }
                }
            }
    }
}

/**
 * Calculates enriched holding data by combining holding information with current price data.
 *
 * @param holding The user's holding information (coin, amount, purchase price, etc.)
 * @param pricesMap Map of coin IDs to their current price data
 * @return HoldingWithPrice containing the holding and all calculated price metrics
 */
private fun calculateHoldingWithPrice(
    holding: Holding,
    pricesMap: Map<String, PriceData>
): HoldingWithPrice {
    val priceInfo = pricesMap[holding.coinId]
    val currentPrice = priceInfo?.usd ?: 0.0
    val usd24hChange = priceInfo?.usd24hChange ?: 0.0

    val currentValue = currentPrice * holding.amount
    val purchaseValue = holding.purchasePrice * holding.amount

    val price24hAgo = if (usd24hChange != 0.0) {
        currentPrice / (1 + (usd24hChange / 100))
    } else {
        currentPrice
    }

    val profitLoss = currentValue - purchaseValue
    val profitLossPercent = if (purchaseValue > 0) {
        (profitLoss / purchaseValue) * 100
    } else {
        0.0
    }

    val profitLoss24h = (currentPrice - price24hAgo) * holding.amount
    val profitLoss24hPercent = if (price24hAgo > 0) {
        ((currentPrice - price24hAgo) / price24hAgo) * 100
    } else {
        0.0
    }

    return HoldingWithPrice(
        holding = holding,
        currentPrice = currentPrice,
        currentValue = currentValue,
        costBasis = purchaseValue,
        profitLoss = profitLoss,
        profitLossPercent = profitLossPercent,
        profitLoss24h = profitLoss24h,
        profitLossPercent24h = profitLoss24hPercent
    )
}
