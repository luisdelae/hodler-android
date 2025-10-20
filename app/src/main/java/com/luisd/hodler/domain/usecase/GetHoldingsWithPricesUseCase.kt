package com.luisd.hodler.domain.usecase

import com.luisd.hodler.domain.model.HoldingWithPrice
import com.luisd.hodler.domain.model.Result
import com.luisd.hodler.domain.repository.CoinRepository
import com.luisd.hodler.domain.repository.PortfolioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetHoldingsWithPricesUseCase @Inject constructor(
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
                            val currentPrice = pricesMap[holding.coinId] ?: 0.0
                            val currentValue = currentPrice * holding.amount
                            val purchaseValue = holding.purchasePrice * holding.amount

                            HoldingWithPrice(
                                holding = holding,
                                currentPrice = currentPrice,
                                currentValue = currentValue,
                                profitLoss = currentValue - purchaseValue,
                                profitLossPercent = currentValue / purchaseValue * 100
                            )
                        }

                        Result.Success(holdingsWithPrices)
                    }
                }
            }
    }
}
