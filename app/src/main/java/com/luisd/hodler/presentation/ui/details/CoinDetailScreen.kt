package com.luisd.hodler.presentation.ui.details

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.luisd.hodler.presentation.theme.HodlerTheme
import com.luisd.hodler.presentation.theme.getProfitLossColor
import com.luisd.hodler.presentation.ui.components.ErrorContent
import com.luisd.hodler.presentation.ui.components.LoadingContent
import com.luisd.hodler.presentation.ui.toUsdFormat

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
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    CoinDetail(coinDetails = state.coinDetail)

                    TimeRangeChips(
                        timeRange = state.timeRange,
                        onSelectedTimeRangeChange = onSelectedTimeRangeChange
                    )

                    MarketChartArea(state = state.chartState, paddingValues = paddingValues)

                    StatsGrid(coinDetails = state.coinDetail, modifier = Modifier)
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
            .padding(horizontal = 16.dp, vertical = 4.dp),
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
            horizontalArrangement = Arrangement.spacedBy(4.dp)
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = coinDetails.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = coinDetails.currentPrice.toUsdFormat(),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            Column(modifier = Modifier.weight(.5f)) {
                Text(
                    text = coinDetails.symbol.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "[ ${if (coinDetails.priceChangePercentage24h > 0) "+" else ""} " +
                            "${coinDetails.priceChangePercentage24h}% ]",
                    style = MaterialTheme.typography.bodyMedium,
                    color = getProfitLossColor(coinDetails.priceChangePercentage24h),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                modifier = Modifier.weight(.5f),
                text = "Rank ${coinDetails.marketCapRank}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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

        is ChartState.Success -> {}
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

@Composable
fun StatsGrid(
    coinDetails: CoinDetail,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier
        .fillMaxWidth()
        .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatCard(
                label = "Market Cap",
                value = coinDetails.marketCapUsd.toString(), // Need to make this compact
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Volume (24h)",
                value = coinDetails.totalVolumeUsd.toString(), // Need to make this compact
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatCard(
                label = "Circulating Supply",
                value = "${coinDetails.circulatingSupply} ${coinDetails.symbol.uppercase()}", // Need to make this compact
                modifier = Modifier.weight(1f),
            )
            StatCard(
                label = "Max Supply",
                value = coinDetails.maxSupply?.let {
                    "${it} ${coinDetails.symbol.uppercase()}" // Need to make this compact
                } ?: "Unlimited",
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatCard(
                label = "All-Time High",
                value = coinDetails.allTimeHighUsd.toUsdFormat(),
                modifier = Modifier.weight(1f),
            )
            StatCard(
                label = "All-Time Low",
                value = coinDetails.allTimeLowUsd.toUsdFormat(),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun StatsGridPreview() {
    HodlerTheme {
        StatsGrid(
            coinDetails = CoinDetail(
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