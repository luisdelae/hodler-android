package com.luisd.hodler.presentation.ui.details

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.luisd.hodler.domain.model.CoinDetail
import com.luisd.hodler.domain.model.MarketChart
import com.luisd.hodler.presentation.theme.HodlerTheme
import com.luisd.hodler.presentation.theme.getProfitLossColor
import com.luisd.hodler.presentation.ui.components.ErrorContent
import com.luisd.hodler.presentation.ui.components.LoadingContent
import com.luisd.hodler.presentation.ui.components.MarketLineChart
import com.luisd.hodler.presentation.ui.util.toCompactFormat
import com.luisd.hodler.presentation.ui.util.toPercentageFormat
import com.luisd.hodler.presentation.ui.util.toUsdFormat
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries

@Composable
fun CoinDetailRoute(
    onNavigateBack: () -> Unit,
    viewModel: CoinDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DetailScreen(
        state = state,
        coinSymbol = viewModel.coinSymbol,
        onNavigateBack = onNavigateBack,
        onSelectedTimeRangeChange = { timeRange -> viewModel.updateTimeRange(timeRange) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    state: CoinDetailUiState,
    coinSymbol: String,
    onNavigateBack: () -> Unit,
    onSelectedTimeRangeChange: (TimeRange) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = coinSymbol.uppercase()) },
                navigationIcon = {
                    TextButton(onClick = { onNavigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                        Text(text = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { }
                    ) {
                        Icon(
                            imageVector = Icons.Default.StarBorder,
                            contentDescription = "Search"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (state) {
            is CoinDetailUiState.Error -> {
                ErrorContent(
                    message = state.message,
                    paddingValues = paddingValues,
                    onRefresh = { },
                )
            }

            CoinDetailUiState.Loading -> {
                LoadingContent(
                    message = "Loading details...",
                    paddingValues = paddingValues,
                )
            }

            is CoinDetailUiState.Success -> {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CoinDetail(coinDetails = state.coinDetail)

                    TimeRangeChips(
                        timeRange = state.timeRange,
                        onSelectedTimeRangeChange = onSelectedTimeRangeChange
                    )

                    MarketChartArea(
                        state = state.chartState,
                        paddingValues = paddingValues,
                        timeRange = state.timeRange
                    )

                    StatsGrid(coinDetail = state.coinDetail)
                }
            }
        }
    }
}

@Composable
fun CoinDetail(
    coinDetails: CoinDetail
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(coinDetails.image)
                    .crossfade(true)
                    .build(),
                contentDescription = "${coinDetails.name} logo",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
            )

            Column(
                modifier = Modifier.weight(1.5f),
            ) {
                Text(
                    text = coinDetails.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = coinDetails.currentPrice.toUsdFormat(),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = coinDetails.symbol.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = coinDetails.priceChangePercentage24h.toPercentageFormat(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = getProfitLossColor(coinDetails.priceChangePercentage24h),
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Text(
                    text = "#${coinDetails.marketCapRank}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
        }
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CoinDetailPreview() {
    HodlerTheme {
        CoinDetail(
            coinDetails = CoinDetail(
                id = "bitcoin",
                name = "Bitcoin but the name is long",
                symbol = "btc",
                image = "",
                currentPrice = 150121.50,
                priceChangePercentage24h = -2.545245,
                marketCapUsd = 845000000000.0,
                marketCapRank = 551,
                totalVolumeUsd = 1.1,
                circulatingSupply = 1.0,
                allTimeHighUsd = 468744.1,
                allTimeLowUsd = 1.0,
                allTimeHighUsdDate = "",
                allTimeLowUsdDate = "",
                totalSupply = null,
                maxSupply = null
            )
        )
    }
}

@Composable
fun TimeRangeChips(
    timeRange: TimeRange,
    onSelectedTimeRangeChange: (TimeRange) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items(TimeRange.entries) { entry ->
            InputChip(
                selected = entry == timeRange,
                onClick = { onSelectedTimeRangeChange(entry) },
                label = { Text(entry.label) },
                colors = InputChipDefaults.inputChipColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun TimeRangeChipsPreview() {
    HodlerTheme {
        TimeRangeChips(
            TimeRange.DAY_7
        ) { }
    }
}

@Composable
fun MarketChartArea(
    state: ChartState,
    paddingValues: PaddingValues,
    timeRange: TimeRange
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp)
    ) {
        when (state) {
            is ChartState.Error -> ErrorContent(
                message = state.message,
                paddingValues = paddingValues,
                onRefresh = { },
            )

            ChartState.Loading -> LoadingContent(
                message = "Loading market chart...",
                paddingValues = paddingValues,
            )

            is ChartState.Success -> {
                MarketLineChart(
                    data = state.chart,
                    timeRange = timeRange,
                )
            }
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun StatCardPreview() {
    HodlerTheme {
        StatCard(
            label = "Market Cap",
            value = "845B",
            modifier = Modifier,
        )
    }
}

data class Stat(val label: String, val value: String)

@Composable
fun StatsGrid(
    coinDetail: CoinDetail,
) {
    val stats = listOf(
        Stat("Market Cap", coinDetail.marketCapUsd.toCompactFormat()),
        Stat("Volume (24h)", coinDetail.totalVolumeUsd.toCompactFormat()),
        Stat(
            "Circulating",
            "${coinDetail.circulatingSupply.toCompactFormat()} ${coinDetail.symbol.uppercase()}"
        ),
        Stat("Max Supply", coinDetail.maxSupply?.toCompactFormat() ?: "Unlimited"),
        Stat("ATH", coinDetail.allTimeHighUsd.toUsdFormat()),
        Stat("ATL", coinDetail.allTimeLowUsd.toUsdFormat()),
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(stats) { stat ->
            StatCard(label = stat.label, value = stat.value)
        }
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun StatsGridPreview() {
    HodlerTheme {
        StatsGrid(
            coinDetail = CoinDetail(
                id = "bitcoin",
                name = "Bitcoin but the name is long",
                symbol = "btc",
                image = "",
                currentPrice = 150121.50,
                priceChangePercentage24h = 2.5,
                marketCapUsd = 845000000000.0,
                marketCapRank = 1,
                totalVolumeUsd = 1.1,
                circulatingSupply = 1.0,
                allTimeHighUsd = 468744.1,
                allTimeLowUsd = 1.0,
                allTimeHighUsdDate = "",
                allTimeLowUsdDate = "",
                totalSupply = null,
                maxSupply = null
            )
        )
    }
}