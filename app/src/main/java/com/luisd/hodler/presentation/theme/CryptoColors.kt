package com.luisd.hodler.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

/**
 * Extended color palette for crypto-specific colors
 * Access via MaterialTheme.cryptoColors
 */
data class CryptoColors(
    val profit: Color,
    val loss: Color,
    val gold: Color,
    val chartBlue: Color,
    val chartGreen: Color,
    val chartYellow: Color,
    val chartOrange: Color,
    val chartRed: Color,
    val chartPurple: Color,
)

/**
 * Light theme crypto colors
 */
private val LightCryptoColors = CryptoColors(
    profit = ProfitGreen,
    loss = Color(0xFFDC2626), // Slightly darker red for light mode
    gold = CryptoGold,
    chartBlue = ChartBlue,
    chartGreen = ChartGreen,
    chartYellow = ChartYellow,
    chartOrange = ChartOrange,
    chartRed = ChartRed,
    chartPurple = ChartPurple,
)

/**
 * Dark theme crypto colors
 */
private val DarkCryptoColors = CryptoColors(
    profit = ProfitGreenLight,
    loss = LossRed,
    gold = CryptoGold,
    chartBlue = ChartBlue,
    chartGreen = ChartGreen,
    chartYellow = ChartYellow,
    chartOrange = ChartOrange,
    chartRed = ChartRed,
    chartPurple = ChartPurple,
)

/**
 * Extension property to access crypto colors from MaterialTheme
 * Usage: MaterialTheme.cryptoColors.profit
 */
val MaterialTheme.cryptoColors: CryptoColors
    @Composable
    @ReadOnlyComposable
    get() = if (MaterialTheme.colorScheme.background == DarkBackground) {
        DarkCryptoColors
    } else {
        LightCryptoColors
    }

/**
 * Helper function to get color based on value (positive/negative)
 * Usage: getProfitLossColor(coin.priceChangePercentage24h)
 */
@Composable
@ReadOnlyComposable
fun getProfitLossColor(value: Double): Color {
    return if (value >= 0) {
        MaterialTheme.cryptoColors.profit
    } else {
        MaterialTheme.cryptoColors.loss
    }
}

/**
 * Helper function to format percentage with color
 * Returns color for the percentage value
 */
@Composable
@ReadOnlyComposable
fun getPercentageColor(percentage: Double): Color = getProfitLossColor(percentage)
