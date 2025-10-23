package com.luisd.hodler.presentation.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.luisd.hodler.presentation.theme.getProfitLossColor
import com.luisd.hodler.presentation.ui.util.toPercentageFormat
import com.luisd.hodler.presentation.ui.util.toUsdFormat


@Composable
fun ProfitLossText(
    profitLoss: Double,
    profitLossPercent: Double,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    val color = getProfitLossColor(profitLoss)

    Text(
        text = "${profitLoss.toUsdFormat()} (${profitLossPercent.toPercentageFormat()})",
        color = color,
        style = style,
        modifier = modifier
    )
}

@Composable
fun ProfitLossPercentText(
    profitLossPercent: Double,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    val color = getProfitLossColor(profitLossPercent)

    Text(
        text = profitLossPercent.toPercentageFormat(),
        color = color,
        style = style,
        modifier = modifier
    )
}