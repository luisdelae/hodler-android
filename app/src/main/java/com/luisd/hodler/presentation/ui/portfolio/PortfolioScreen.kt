package com.luisd.hodler.presentation.ui.portfolio

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.luisd.hodler.domain.model.PortfolioSummary
import com.luisd.hodler.presentation.theme.HodlerTheme
import com.luisd.hodler.presentation.ui.components.CacheIndicatorBanner
import com.luisd.hodler.presentation.ui.components.ErrorContent
import com.luisd.hodler.presentation.ui.components.LoadingContent
import com.luisd.hodler.presentation.ui.portfolio.components.PortfolioEmptySection
import com.luisd.hodler.presentation.ui.portfolio.components.PortfolioSection
import com.luisd.hodler.presentation.ui.portfolio.components.getSampleBitcoinGroup
import com.luisd.hodler.presentation.ui.portfolio.components.getSampleCoinGroupsWithLosses
import com.luisd.hodler.presentation.ui.portfolio.components.getSampleCoinGroupsWithMultipleHoldings
import com.luisd.hodler.presentation.ui.portfolio.components.getSampleCoinGroupsWithProfits
import com.luisd.hodler.presentation.ui.portfolio.components.getSampleManyCoinGroups
import com.luisd.hodler.presentation.ui.portfolio.components.getSampleMixedPerformanceCoinGroups
import com.luisd.hodler.presentation.ui.portfolio.components.getSamplePortfolioSummaryWithLosses
import com.luisd.hodler.presentation.ui.portfolio.components.getSamplePortfolioSummaryWithProfits

@Composable
fun PortfolioRoute(
    outerPaddingValues: PaddingValues,
    onAddHoldingClick: () -> Unit,
    onEditHolding: (Long) -> Unit,
    onNavigateToCoinDetail: (String, String) -> Unit,
    viewModel: PortfolioViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PortfolioScreen(
        outerPaddingValues = outerPaddingValues,
        uiState = uiState,
        onRefresh = { viewModel.refreshPrices() },
        onAddHoldingClick = onAddHoldingClick,
        onEditHolding = onEditHolding,
        onDeleteHolding = viewModel::deleteHolding,
        onNavigateToCoinDetail = onNavigateToCoinDetail,
        onToggleCoinExpansion = viewModel::toggleCoinExpansion,
    )
}

@Composable
fun PortfolioScreen(
    outerPaddingValues: PaddingValues,
    uiState: PortfolioUiState,
    onRefresh: () -> Unit,
    onAddHoldingClick: () -> Unit,
    onEditHolding: (Long) -> Unit,
    onDeleteHolding: (Long) -> Unit,
    onNavigateToCoinDetail: (String, String) -> Unit,
    onToggleCoinExpansion: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            Column {
                TopBar(
                    uiState = uiState,
                    onAddHoldingClick = onAddHoldingClick
                )

                if (uiState is PortfolioUiState.Success && uiState.isFromCache) {
                    CacheIndicatorBanner(
                        lastUpdated = uiState.lastUpdated,
                        onRefresh = onRefresh
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(outerPaddingValues)
        ) {
            when (uiState) {
                is PortfolioUiState.Loading -> {
                    LoadingContent(
                        message = "Loading portfolio...",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    )
                }

                is PortfolioUiState.Error -> {
                    ErrorContent(
                        message = "Failed to load portfolio",
                        onRefresh = onRefresh,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    )
                }

                is PortfolioUiState.Empty -> {
                    PortfolioEmptySection(
                        onAddNewHolding = onAddHoldingClick,
                        modifier = Modifier.padding(paddingValues)
                    )
                }

                is PortfolioUiState.Success -> {
                    PortfolioSection(
                        uiState = uiState,
                        modifier = Modifier.padding(paddingValues),
                        onNavigateToCoinDetail = onNavigateToCoinDetail,
                        onEditHolding = onEditHolding,
                        onDeleteHolding = onDeleteHolding,
                        onToggleCoinExpansion = onToggleCoinExpansion,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    uiState: PortfolioUiState,
    onAddHoldingClick: () -> Unit,
) {
    TopAppBar(
        title = { Text(text = "Portfolio") },
        actions = {
            if (uiState is PortfolioUiState.Success) {
                IconButton(
                    onClick = { onAddHoldingClick() }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add holding",
                    )
                }
            }
        }
    )
}

// ============================================================
// PortfolioScreen Previews
// ============================================================

@Preview(name = "Light: Portfolio - Loading State", showBackground = true)
@Preview(
    name = "Dark: Portfolio - Loading State",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewPortfolioScreenLoading() {
    HodlerTheme {
        PortfolioScreen(
            outerPaddingValues = PaddingValues(0.dp),
            uiState = PortfolioUiState.Loading,
            onRefresh = { },
            onAddHoldingClick = { },
            onEditHolding = { },
            onDeleteHolding = { },
            onNavigateToCoinDetail = { _, _ -> },
            onToggleCoinExpansion = { },
        )
    }
}

@Preview(name = "Light: Portfolio - Empty State", showBackground = true)
@Preview(name = "Dark: Portfolio - Empty State", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewPortfolioScreenEmpty() {
    HodlerTheme {
        PortfolioScreen(
            outerPaddingValues = PaddingValues(0.dp),
            uiState = PortfolioUiState.Empty,
            onRefresh = { },
            onAddHoldingClick = { },
            onEditHolding = { },
            onDeleteHolding = { },
            onNavigateToCoinDetail = { _, _ -> },
            onToggleCoinExpansion = { },
        )
    }
}

@Preview(name = "Light: Portfolio - Error State", showBackground = true)
@Preview(name = "Dark: Portfolio - Error State", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewPortfolioScreenError() {
    HodlerTheme {
        PortfolioScreen(
            outerPaddingValues = PaddingValues(0.dp),
            uiState = PortfolioUiState.Error("Failed to load portfolio data. Please try again."),
            onRefresh = { },
            onAddHoldingClick = { },
            onEditHolding = { },
            onDeleteHolding = { },
            onNavigateToCoinDetail = { _, _ -> },
            onToggleCoinExpansion = { },
        )
    }
}

@Preview(name = "Light: Portfolio - Success with profits", showBackground = true)
@Preview(
    name = "Dark: Portfolio - Success with profits",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewPortfolioScreenSuccessProfits() {
    HodlerTheme {
        PortfolioScreen(
            outerPaddingValues = PaddingValues(0.dp),
            uiState = PortfolioUiState.Success(
                summary = getSamplePortfolioSummaryWithProfits(),
                holdings = getSampleCoinGroupsWithProfits(),
                isRefreshing = false,
                expandedCoinIds = emptySet()
            ),
            onRefresh = { },
            onAddHoldingClick = { },
            onEditHolding = { },
            onDeleteHolding = { },
            onNavigateToCoinDetail = { _, _ -> },
            onToggleCoinExpansion = { },
        )
    }
}

@Preview(name = "Light: Portfolio - Success with losses", showBackground = true)
@Preview(
    name = "Dark: Portfolio - Success with losses",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewPortfolioScreenSuccessLosses() {
    HodlerTheme {
        PortfolioScreen(
            outerPaddingValues = PaddingValues(0.dp),
            uiState = PortfolioUiState.Success(
                summary = getSamplePortfolioSummaryWithLosses(),
                holdings = getSampleCoinGroupsWithLosses(),
                isRefreshing = false,
                expandedCoinIds = emptySet()
            ),
            onRefresh = { },
            onAddHoldingClick = { },
            onEditHolding = { },
            onDeleteHolding = { },
            onNavigateToCoinDetail = { _, _ -> },
            onToggleCoinExpansion = { },
        )
    }
}

@Preview(name = "Light: Portfolio - Success with expanded coins", showBackground = true)
@Preview(
    name = "Dark: Portfolio - Success with expanded coins",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewPortfolioScreenExpanded() {
    HodlerTheme {
        PortfolioScreen(
            outerPaddingValues = PaddingValues(0.dp),
            uiState = PortfolioUiState.Success(
                summary = getSamplePortfolioSummaryWithProfits(),
                holdings = getSampleCoinGroupsWithMultipleHoldings(),
                isRefreshing = false,
                expandedCoinIds = setOf("bitcoin", "ethereum")
            ),
            onRefresh = { },
            onAddHoldingClick = { },
            onEditHolding = { },
            onDeleteHolding = { },
            onNavigateToCoinDetail = { _, _ -> },
            onToggleCoinExpansion = { },
        )
    }
}

@Preview(name = "Light: Portfolio - Single coin", showBackground = true)
@Preview(name = "Dark: Portfolio - Single coin", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewPortfolioScreenSingleCoin() {
    HodlerTheme {
        PortfolioScreen(
            outerPaddingValues = PaddingValues(0.dp),
            uiState = PortfolioUiState.Success(
                summary = PortfolioSummary(
                    coinsOwned = 1,
                    totalValue = 67234.56,
                    totalCostBasis = 50000.0,
                    totalProfitLoss = 17234.56,
                    totalProfitLossPercent = 34.47,
                    totalProfitLoss24h = 2310.45,
                    totalProfitLossPercent24h = 3.56
                ),
                holdings = listOf(getSampleBitcoinGroup()),
                isRefreshing = false,
                expandedCoinIds = emptySet()
            ),
            onRefresh = { },
            onAddHoldingClick = { },
            onEditHolding = { },
            onDeleteHolding = { },
            onNavigateToCoinDetail = { _, _ -> },
            onToggleCoinExpansion = { },
        )
    }
}

@Preview(name = "Light: Portfolio - Many coins", showBackground = true)
@Preview(name = "Dark: Portfolio - Many coins", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewPortfolioScreenManyCoins() {
    HodlerTheme {
        PortfolioScreen(
            outerPaddingValues = PaddingValues(0.dp),
            uiState = PortfolioUiState.Success(
                summary = PortfolioSummary(
                    coinsOwned = 10,
                    totalValue = 125000.0,
                    totalCostBasis = 100000.0,
                    totalProfitLoss = 25000.0,
                    totalProfitLossPercent = 25.0,
                    totalProfitLoss24h = 3500.0,
                    totalProfitLossPercent24h = 2.88
                ),
                holdings = getSampleManyCoinGroups(),
                isRefreshing = false,
                expandedCoinIds = emptySet()
            ),
            onRefresh = { },
            onAddHoldingClick = { },
            onEditHolding = { },
            onDeleteHolding = { },
            onNavigateToCoinDetail = { _, _ -> },
            onToggleCoinExpansion = { },
        )
    }
}

@Preview(name = "Light: Portfolio - Mixed performance", showBackground = true)
@Preview(
    name = "Dark: Portfolio - Mixed performance",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewPortfolioScreenMixedPerformance() {
    HodlerTheme {
        PortfolioScreen(
            outerPaddingValues = PaddingValues(0.dp),
            uiState = PortfolioUiState.Success(
                summary = PortfolioSummary(
                    coinsOwned = 5,
                    totalValue = 85000.0,
                    totalCostBasis = 80000.0,
                    totalProfitLoss = 5000.0,
                    totalProfitLossPercent = 6.25,
                    totalProfitLoss24h = -1200.0,
                    totalProfitLossPercent24h = -1.39
                ),
                holdings = getSampleMixedPerformanceCoinGroups(),
                isRefreshing = false,
                expandedCoinIds = emptySet()
            ),
            onRefresh = { },
            onAddHoldingClick = { },
            onEditHolding = { },
            onDeleteHolding = { },
            onNavigateToCoinDetail = { _, _ -> },
            onToggleCoinExpansion = { },
        )
    }
}
