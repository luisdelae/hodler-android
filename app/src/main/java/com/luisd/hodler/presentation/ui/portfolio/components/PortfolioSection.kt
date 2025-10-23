package com.luisd.hodler.presentation.ui.portfolio.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.luisd.hodler.domain.model.Holding
import com.luisd.hodler.domain.model.HoldingWithPrice
import com.luisd.hodler.domain.model.PortfolioSummary
import com.luisd.hodler.presentation.theme.HodlerTheme
import com.luisd.hodler.presentation.ui.portfolio.CoinGroup
import com.luisd.hodler.presentation.ui.portfolio.PortfolioUiState

@Composable
fun PortfolioSection(
    state: PortfolioUiState.Success,
) {
    val expandedCoinIds = remember { mutableStateSetOf<String>() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            PortfolioSummarySection(
                portfolioSummary = state.summary,
            )
        }

        item {
            Text(
                text = "Holdings",
                modifier = Modifier.padding(16.dp),
            )
        }

        state.holdings.forEach { coinGroup ->
            item(key = coinGroup.coinId) {
                CoinGroupCard(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    coinGroup = coinGroup,
                    isExpanded = expandedCoinIds.contains(coinGroup.coinId),
                    onToggle = {
                        if (expandedCoinIds.contains(coinGroup.coinId)) {
                            expandedCoinIds.remove(coinGroup.coinId)
                        } else {
                            expandedCoinIds.add(coinGroup.coinId)
                        }
                    }
                )
            }

            if (expandedCoinIds.contains(coinGroup.coinId)) {
                items(
                    items = coinGroup.holdings,
                    key = { it.holding.id }
                ) { holding ->
                    IndividualHoldingCard(holding = holding)
                }
            }
        }
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PortfolioSectionPreview() {
    HodlerTheme {
        PortfolioSection(
            state = PortfolioUiState.Success(
                summary = PortfolioSummary(
                    coinsOwned = 2,
                    totalValue = 12847.32,
                    totalCostBasis = 11600.00,
                    totalProfitLoss = 1247.32,
                    totalProfitLossPercent = 10.75,
                    totalProfitLoss24h = 287.45,
                    totalProfitLossPercent24h = 2.29
                ),
                holdings = listOf(
                    CoinGroup(
                        coinId = "bitcoin",
                        coinSymbol = "BTC",
                        coinName = "Bitcoin",
                        imageUrl = "",
                        totalAmount = 0.15,
                        averageCostBasis = 50300.0,
                        totalCurrentValue = 8134.67,
                        totalProfitLoss = 859.67,
                        totalProfitLossPercent = 11.8,
                        holdings = listOf(
                            HoldingWithPrice(
                                holding = Holding(
                                    id = 1,
                                    coinId = "bitcoin",
                                    coinSymbol = "BTC",
                                    coinName = "Bitcoin",
                                    amount = 0.15,
                                    purchasePrice = 48500.0,
                                    purchaseDate = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000,
                                    imageUrl = ""
                                ),
                                currentPrice = 54231.12,
                                currentValue = 8134.67,
                                costBasis = 7275.0,
                                profitLoss = 859.67,
                                profitLossPercent = 11.8,
                                profitLoss24h = 37.50,
                                profitLossPercent24h = 2.5
                            )
                        ),
                        holdingCount = 1
                    ),
                    CoinGroup(
                        coinId = "ethereum",
                        coinSymbol = "ETH",
                        coinName = "Ethereum",
                        imageUrl = "",
                        totalAmount = 2.5,
                        averageCostBasis = 1600.0,
                        totalCurrentValue = 10031.45,
                        totalProfitLoss = 6031.45,
                        totalProfitLossPercent = 150.8,
                        holdings = listOf(
                            HoldingWithPrice(
                                holding = Holding(
                                    id = 2,
                                    coinId = "ethereum",
                                    coinSymbol = "ETH",
                                    coinName = "Ethereum",
                                    amount = 1.5,
                                    purchasePrice = 1500.0,
                                    purchaseDate = System.currentTimeMillis() - 60L * 24 * 60 * 60 * 1000,
                                    imageUrl = ""
                                ),
                                currentPrice = 4012.58,
                                currentValue = 6018.87,
                                costBasis = 2250.0,
                                profitLoss = 3768.87,
                                profitLossPercent = 167.5,
                                profitLoss24h = 60.19,
                                profitLossPercent24h = 1.5
                            ),
                            HoldingWithPrice(
                                holding = Holding(
                                    id = 3,
                                    coinId = "ethereum",
                                    coinSymbol = "ETH",
                                    coinName = "Ethereum",
                                    amount = 1.0,
                                    purchasePrice = 1750.0,
                                    purchaseDate = System.currentTimeMillis() - 45L * 24 * 60 * 60 * 1000,
                                    imageUrl = ""
                                ),
                                currentPrice = 4012.58,
                                currentValue = 4012.58,
                                costBasis = 1750.0,
                                profitLoss = 2262.58,
                                profitLossPercent = 129.3,
                                profitLoss24h = 40.13,
                                profitLossPercent24h = 1.5
                            )
                        ),
                        holdingCount = 2
                    )
                )
            )
        )
    }
}
