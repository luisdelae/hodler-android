package com.luisd.hodler.presentation.ui.details.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luisd.hodler.domain.model.MarketChart
import com.luisd.hodler.domain.model.PricePoint
import com.luisd.hodler.presentation.theme.HodlerTheme
import com.luisd.hodler.presentation.ui.components.ErrorContent
import com.luisd.hodler.presentation.ui.components.LoadingContent
import com.luisd.hodler.presentation.ui.details.ChartState
import com.luisd.hodler.presentation.ui.details.TimeRange
import com.luisd.hodler.presentation.ui.util.getMockPriceData
import com.luisd.hodler.presentation.ui.util.timeStampChartFormat
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import kotlinx.coroutines.runBlocking

@Composable
fun CoinDetailChartSection(
    state: ChartState,
    paddingValues: PaddingValues,
    timeRange: TimeRange,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
//            .height(300.dp)
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
                CoinDetailLineChart(
                    data = state.chart,
                    timeRange = timeRange,
                )
            }
        }
    }
}

@Composable
private fun CoinDetailLineChartContent(
    modelProducer: CartesianChartModelProducer,
    prices: List<PricePoint>,
    timeRange: TimeRange,
    modifier: Modifier = Modifier,
) {
    val maxPrice: Double = prices.maxOf { it.price }
    val minPrice: Double = prices.minOf { it.price }
    val priceRange = maxPrice - minPrice
    val padding = priceRange * .1

    CartesianChartHost(
        chart =
            rememberCartesianChart(
                rememberLineCartesianLayer(
                    rangeProvider = CartesianLayerRangeProvider.fixed(
                        minY = minPrice - padding,
                        maxY = maxPrice + padding
                    )
                ),
                startAxis = VerticalAxis.rememberStart(
                    label = rememberAxisLabelComponent(),
                    guideline = null,
                    itemPlacer = VerticalAxis.ItemPlacer.count(
                        count = { 3 }
                    ),
                    valueFormatter = { _, value, _ ->
                        when {
                            value >= 1_000_000 -> "$%.0fM".format(value / 1_000_000)
                            value >= 1_000 -> "$%.0fK".format(value / 1_000)
                            value >= 0.01 -> "$%.2f".format(value)
                            value >= 0.00001 -> "$%.5f".format(value)
                            else -> "$%.2e".format(value)
                        }
                    }
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    label = rememberAxisLabelComponent(
                        textSize = 12.sp
                    ),
                    itemPlacer = HorizontalAxis.ItemPlacer.aligned(
                        spacing = {
                            when (timeRange) {
                                // Show label every 4h, 24h, 5d, 30d
                                TimeRange.DAY_1 -> 4
                                TimeRange.DAY_7 -> 24
                                TimeRange.DAY_30 -> 5
                                TimeRange.YEAR_1 -> 30
                            }
                        },
                    ),
                    valueFormatter = { _, value, _ ->
                        val index = value.toInt().coerceIn(0, prices.lastIndex)
                        prices[index].timestamp.timeStampChartFormat(timeRange)
                    }
                ),
            ),
        modelProducer = modelProducer,
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        scrollState = rememberVicoScrollState(
            scrollEnabled = false
        )
    )
}

@Composable
fun CoinDetailLineChart(
    data: MarketChart,
    timeRange: TimeRange,
    modifier: Modifier = Modifier
) {
    if (data.prices.isEmpty()) {
        ErrorContent(
            message = "No data to display",
            paddingValues = PaddingValues(0.dp)
        ) { }
        return
    }

    val modelProducer = remember { CartesianChartModelProducer() }

    var chartError by remember { mutableStateOf<String?>(null) }

    val displayPrices = remember(data.prices, timeRange) {
        val maxPoints = when (timeRange) {
            TimeRange.DAY_1 -> 48
            TimeRange.DAY_7 -> 84
            TimeRange.DAY_30 -> 90
            TimeRange.YEAR_1 -> 120
        }

        if (data.prices.size <= maxPoints) {
            data.prices
        } else {
            val step = data.prices.size.toFloat() / maxPoints
            data.prices.filterIndexed { index, _ ->
                (index % step.toInt() == 0) || index == data.prices.lastIndex
            }
        }
    }

    LaunchedEffect(displayPrices) {
        chartError = null

        try {
            modelProducer.runTransaction {
                lineSeries {
                    series(
                        x = displayPrices.indices.map { it.toFloat() },
                        y = displayPrices.map { it.price },
                    )
                }
            }
        } catch (e: Exception) {
            chartError = "Failed to load chart"
            // TODO: Log after Trimber impl
        }
    }

    if (chartError != null) {
        ErrorContent(
            message = chartError!!,
            paddingValues = PaddingValues(0.dp)
        ) { }
    } else {
        CoinDetailLineChartContent(
            modelProducer = modelProducer,
            prices = displayPrices,
            timeRange = timeRange,
            modifier = modifier,
        )
    }
}

@Preview(name = "Light", showBackground = true)
@Composable
private fun Preview24h() {
    val modelProducer = remember { CartesianChartModelProducer() }
    val timeRange = TimeRange.DAY_1
    val data = getMockPriceData(TimeRange.DAY_1)

    runBlocking {
        modelProducer.runTransaction {
            lineSeries {
                this.series(
                    x = data.indices.map { it.toFloat() },
                    y = data.map { it.price },
                )
            }
        }
    }
    HodlerTheme {
        CoinDetailLineChartContent(modelProducer, data, timeRange)
    }
}

@Preview(name = "Light", showBackground = true)
@Composable
private fun Preview30d() {
    val modelProducer = remember { CartesianChartModelProducer() }
    val timeRange = TimeRange.DAY_30
    val prices = getMockPriceData(TimeRange.DAY_30)

    val displayPrices = remember(prices) {
        val maxPoints = when (timeRange) {
            TimeRange.DAY_1 -> 48
            TimeRange.DAY_7 -> 84
            TimeRange.DAY_30 -> 90
            TimeRange.YEAR_1 -> 120
        }

        if (prices.size <= maxPoints) {
            prices
        } else {
            val step = prices.size.toFloat() / maxPoints
            prices.filterIndexed { index, _ ->
                (index % step.toInt() == 0) || index == prices.lastIndex
            }
        }
    }

    runBlocking {
        modelProducer.runTransaction {
            lineSeries {
                this.series(
                    x = displayPrices.indices.map { it.toFloat() },
                    y = displayPrices.map { it.price },
                )
            }
        }
    }
    HodlerTheme {
        CoinDetailLineChartContent(modelProducer, displayPrices, timeRange)
    }
}
