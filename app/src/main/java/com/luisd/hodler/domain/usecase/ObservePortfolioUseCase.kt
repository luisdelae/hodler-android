package com.luisd.hodler.domain.usecase

import com.luisd.hodler.domain.model.HoldingWithPrice
import com.luisd.hodler.domain.model.Result
import com.luisd.hodler.domain.repository.CoinRepository
import com.luisd.hodler.domain.repository.PortfolioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObservePortfolioUseCase @Inject constructor(
    private val portfolioRepository: PortfolioRepository,
    private val coinRepository: CoinRepository,
) {
    operator fun invoke(): Flow<Result<List<HoldingWithPrice>>> {
        return portfolioRepository.getAllHoldings()
            .map { holdingResult ->
                when (holdingResult) {
                    is Result.Error -> Result.Error(holdingResult.exception)
                    Result.Loading -> Result.Loading
                    is Result.Success -> {
                        val holdings = holdingResult.data
                        val coinIds = holdings.map { it.coinId }.distinct()

                        val pricesMap =
                            when (val pricesResult = coinRepository.getCurrentPrices(coinIds)) {
                                is Result.Success -> pricesResult.data
                                else -> emptyMap()
                            }

                        val holdingsWithPrices = holdings.map { holding ->
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
                            val profitLossPercent = (profitLoss / purchaseValue) * 100

                            val profitLoss24h = (currentPrice - price24hAgo) * holding.amount
                            val profitLoss24hPercent = ((currentPrice - price24hAgo) / price24hAgo) * 100

                            HoldingWithPrice(
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

                        Result.Success(holdingsWithPrices)
                    }
                }
            }
    }
}
