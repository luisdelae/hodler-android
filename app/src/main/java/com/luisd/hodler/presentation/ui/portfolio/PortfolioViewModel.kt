package com.luisd.hodler.presentation.ui.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisd.hodler.domain.model.HoldingWithPrice
import com.luisd.hodler.domain.model.PortfolioSummary
import com.luisd.hodler.domain.model.Result
import com.luisd.hodler.domain.usecase.GetHoldingsWithPricesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val getHoldingsWithPricesUseCase: GetHoldingsWithPricesUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<Result<Portfolio>>(Result.Loading)
    val state: StateFlow<Result<Portfolio>> = _state.asStateFlow()

    init {
        loadPortfolio()
    }

    private fun loadPortfolio() {
        viewModelScope.launch {
            getHoldingsWithPricesUseCase().collect { result ->
                _state.value = when (result) {
                    is Result.Success -> {
                        Result.Success(
                            Portfolio(
                                portfolioSummary = aggregateSummary(result.data),
                                holdingsWithPrice = result.data
                            )
                        ) as Result<Portfolio>
                    }

                    is Result.Loading -> Result.Loading
                    is Result.Error -> Result.Error(result.exception)
                }
            }
        }
    }

    fun refresh() {
        loadPortfolio()
    }

    private fun aggregateSummary(holdings: List<HoldingWithPrice>): PortfolioSummary {
        val coinsOwned = holdings.distinctBy { it.holding.coinId }.size
        val totalCurrentValue = holdings.sumOf { it.currentPrice }
        val totalProfitLoss = holdings.sumOf { it.profitLoss }
        val totalProfitLoss24h = holdings.sumOf { it.profitLoss24h }
        val totalCostBasis = holdings.sumOf { it.costBasis }

        val totalProfileLossPercent = if (totalCostBasis > 0.0) {
            (totalProfitLoss / totalCostBasis) * 100
        } else 0.0

        val portfolioValue24hAgo = totalCurrentValue - totalProfitLoss24h
        val totalProfitLossPercent24h = if (portfolioValue24hAgo > 0) {
            (totalProfitLoss24h / portfolioValue24hAgo) * 100
        } else 0.0

        return PortfolioSummary(
            coinsOwned = coinsOwned,
            totalValue = totalCurrentValue,
            totalCostBasis = totalCostBasis,
            totalProfitLoss = totalProfitLoss,
            totalProfitLossPercent = totalProfileLossPercent,
            totalProfitLoss24h = totalProfitLoss24h,
            totalProfitLossPercent24h = totalProfitLossPercent24h
        )
    }
}

data class Portfolio(
    val portfolioSummary: PortfolioSummary,
    val holdingsWithPrice: List<HoldingWithPrice>
)