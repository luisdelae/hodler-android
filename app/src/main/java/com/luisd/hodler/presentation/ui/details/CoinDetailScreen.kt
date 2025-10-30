package com.luisd.hodler.presentation.ui.details

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.luisd.hodler.presentation.theme.HodlerTheme
import com.luisd.hodler.presentation.ui.components.CacheIndicatorBanner
import com.luisd.hodler.presentation.ui.components.ErrorContent
import com.luisd.hodler.presentation.ui.components.LoadingContent
import com.luisd.hodler.presentation.ui.details.components.CoinDetailCard
import com.luisd.hodler.presentation.ui.details.components.CoinDetailChartSection
import com.luisd.hodler.presentation.ui.details.components.CoinDetailStatsSection
import com.luisd.hodler.presentation.ui.details.components.TimeRangeChips
import com.luisd.hodler.presentation.ui.details.components.getSampleBitcoinDetail
import com.luisd.hodler.presentation.ui.details.components.getSampleCoinWithNegativeChange
import com.luisd.hodler.presentation.ui.details.components.getSampleCoinWithNullSupply
import com.luisd.hodler.presentation.ui.details.components.getSampleEthereumDetail
import com.luisd.hodler.presentation.ui.details.components.getSampleMarketChart24H

@Composable
fun CoinDetailRoute(
    onNavigateBack: () -> Unit,
    onAddToPortfolio: (String) -> Unit,
    viewModel: CoinDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val onTimeRangeChange = remember {
        { timeRange: TimeRange -> viewModel.updateTimeRange(timeRange) }
    }

    DetailScreen(
        uiState = uiState,
        coinSymbol = viewModel.coinSymbol,
        onRefresh = viewModel::refresh,
        onNavigateBack = onNavigateBack,
        onSelectedTimeRangeChange = { timeRange -> onTimeRangeChange(timeRange) },
        onAddToPortfolio = onAddToPortfolio
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    uiState: CoinDetailUiState,
    coinSymbol: String,
    onRefresh: () -> Unit,
    onNavigateBack: () -> Unit,
    onSelectedTimeRangeChange: (TimeRange) -> Unit,
    onAddToPortfolio: (String) -> Unit
) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = coinSymbol.uppercase(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                )

                if (uiState is CoinDetailUiState.Success && uiState.isFromCache) {
                    CacheIndicatorBanner(
                        lastUpdated = uiState.lastUpdated,
                        onRefresh = onRefresh
                    )
                }
            }
        }
    ) { paddingValues ->
        when (uiState) {
            is CoinDetailUiState.Error -> {
                ErrorContent(
                    message = uiState.message,
                    onRefresh = onRefresh,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            CoinDetailUiState.Loading -> {
                LoadingContent(
                    message = "Loading details...",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            is CoinDetailUiState.Success -> {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CoinDetailCard(coinDetails = uiState.coinDetail)

                    TimeRangeChips(
                        timeRange = uiState.timeRange,
                        onSelectedTimeRangeChange = onSelectedTimeRangeChange
                    )

                    CoinDetailChartSection(
                        uiState = uiState.chartState,
                        timeRange = uiState.timeRange,
                    )

                    CoinDetailStatsSection(coinDetail = uiState.coinDetail)

                    TextButton(
                        onClick = { onAddToPortfolio(uiState.coinDetail.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = "Add to portfolio",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }
        }
    }
}

// ============================================================
// CoinDetailScreen Previews
// ============================================================

@Preview(name = "Light: Detail - Loading State", showBackground = true)
@Preview(name = "Dark: Detail - Loading State", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewDetailScreenLoading() {
    HodlerTheme {
        DetailScreen(
            uiState = CoinDetailUiState.Loading,
            coinSymbol = "BTC",
            onRefresh = {},
            onNavigateBack = {},
            onSelectedTimeRangeChange = {},
            onAddToPortfolio = {}
        )
    }
}

@Preview(name = "Light: Detail - Error State", showBackground = true)
@Preview(name = "Dark: Detail - Error State", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewDetailScreenError() {
    HodlerTheme {
        DetailScreen(
            uiState = CoinDetailUiState.Error("Failed to load coin details. Please check your connection."),
            coinSymbol = "BTC",
            onRefresh = {},
            onNavigateBack = {},
            onSelectedTimeRangeChange = {},
            onAddToPortfolio = {}
        )
    }
}

@Preview(name = "Light: Detail - Success with Chart Loading", showBackground = true)
@Preview(
    name = "Dark: Detail - Success with Chart Loading",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewDetailScreenSuccessChartLoading() {
    HodlerTheme {
        DetailScreen(
            uiState = CoinDetailUiState.Success(
                coinDetail = getSampleBitcoinDetail(),
                chartState = ChartState.Loading,
                timeRange = TimeRange.DAY_1
            ),
            coinSymbol = "BTC",
            onRefresh = {},
            onNavigateBack = {},
            onSelectedTimeRangeChange = {},
            onAddToPortfolio = {}
        )
    }
}

@Preview(name = "Light: Detail - Success with Chart 24H", showBackground = true)
@Preview(
    name = "Dark: Detail - Success with Chart 24H",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewDetailScreenSuccess24H() {
    HodlerTheme {
        DetailScreen(
            uiState = CoinDetailUiState.Success(
                coinDetail = getSampleBitcoinDetail(),
                chartState = ChartState.Success(getSampleMarketChart24H()),
                timeRange = TimeRange.DAY_1
            ),
            coinSymbol = "BTC",
            onRefresh = {},
            onNavigateBack = {},
            onSelectedTimeRangeChange = {},
            onAddToPortfolio = {}
        )
    }
}

@Preview(name = "Light: Detail - Success with Chart Error", showBackground = true)
@Preview(
    name = "Dark: Detail - Success with Chart Error",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewDetailScreenSuccessChartError() {
    HodlerTheme {
        DetailScreen(
            uiState = CoinDetailUiState.Success(
                coinDetail = getSampleBitcoinDetail(),
                chartState = ChartState.Error("Failed to load chart data"),
                timeRange = TimeRange.DAY_1
            ),
            coinSymbol = "BTC",
            onRefresh = {},
            onNavigateBack = {},
            onSelectedTimeRangeChange = {},
            onAddToPortfolio = {}
        )
    }
}

@Preview(name = "Light: Detail - Coin with Positive Change", showBackground = true)
@Preview(
    name = "Dark: Detail - Coin with Positive Change",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewDetailScreenEthereum() {
    HodlerTheme {
        DetailScreen(
            uiState = CoinDetailUiState.Success(
                coinDetail = getSampleEthereumDetail(),
                chartState = ChartState.Success(getSampleMarketChart24H()),
                timeRange = TimeRange.DAY_7
            ),
            coinSymbol = "ETH",
            onRefresh = {},
            onNavigateBack = {},
            onSelectedTimeRangeChange = {},
            onAddToPortfolio = {}
        )
    }
}

@Preview(name = "Light: Detail - Coin with Negative Change", showBackground = true)
@Preview(
    name = "Dark: Detail - Coin with Negative Change",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewDetailScreenNegativeChange() {
    HodlerTheme {
        DetailScreen(
            uiState = CoinDetailUiState.Success(
                coinDetail = getSampleCoinWithNegativeChange(),
                chartState = ChartState.Success(getSampleMarketChart24H()),
                timeRange = TimeRange.DAY_1
            ),
            coinSymbol = "ADA",
            onRefresh = {},
            onNavigateBack = {},
            onSelectedTimeRangeChange = {},
            onAddToPortfolio = {}
        )
    }
}

@Preview(name = "Light: Detail - Coin with Null Supply Values", showBackground = true)
@Preview(
    name = "Dark: Detail - Coin with Null Supply Values",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewDetailScreenNullSupply() {
    HodlerTheme {
        DetailScreen(
            uiState = CoinDetailUiState.Success(
                coinDetail = getSampleCoinWithNullSupply(),
                chartState = ChartState.Success(getSampleMarketChart24H()),
                timeRange = TimeRange.DAY_30
            ),
            coinSymbol = "DOGE",
            onRefresh = {},
            onNavigateBack = {},
            onSelectedTimeRangeChange = {},
            onAddToPortfolio = {}
        )
    }
}

@Preview(name = "Light: Detail - Long Coin Symbol\"", showBackground = true)
@Preview(
    name = "Dark: Detail - Long Coin Symbol",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewDetailScreenLongSymbol() {
    HodlerTheme {
        DetailScreen(
            uiState = CoinDetailUiState.Success(
                coinDetail = getSampleBitcoinDetail(),
                chartState = ChartState.Success(getSampleMarketChart24H()),
                timeRange = TimeRange.DAY_1
            ),
            coinSymbol = "SUPERVERYLONGCOINEXAMPLEOVERFLOWTEST",
            onRefresh = {},
            onNavigateBack = {},
            onSelectedTimeRangeChange = {},
            onAddToPortfolio = {}
        )
    }
}
