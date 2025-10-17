package com.luisd.hodler.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luisd.hodler.domain.model.MarketChart
import com.luisd.hodler.domain.model.PricePoint
import com.luisd.hodler.presentation.theme.HodlerTheme
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
private fun MarketLineChart(
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
fun MarketLineChart(
    data: MarketChart,
    timeRange: TimeRange,
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    val displayPrices = remember(data.prices) {
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

    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            lineSeries {
                series(
                    x = displayPrices.indices.map { it.toFloat() },
                    y = displayPrices.map { it.price },
                )
            }
        }
    }
    MarketLineChart(modelProducer, displayPrices, timeRange, modifier)
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
        MarketLineChart(modelProducer, data, timeRange)
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
        MarketLineChart(modelProducer, displayPrices, timeRange)
    }
}
