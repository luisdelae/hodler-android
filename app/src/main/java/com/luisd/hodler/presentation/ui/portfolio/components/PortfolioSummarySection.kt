package com.luisd.hodler.presentation.ui.portfolio.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.luisd.hodler.domain.model.PortfolioSummary
import com.luisd.hodler.presentation.theme.HodlerTheme
import com.luisd.hodler.presentation.ui.components.Stat
import com.luisd.hodler.presentation.ui.components.StatsGrid
import com.luisd.hodler.presentation.ui.util.toPercentageFormat
import com.luisd.hodler.presentation.ui.util.toUsdFormat

@Composable
fun PortfolioSummarySection(
    portfolioSummary: PortfolioSummary,
    modifier: Modifier = Modifier,
) {
    val stats = remember(portfolioSummary) {
        listOf(
            Stat("Total Value", portfolioSummary.totalValue.toUsdFormat()),
            Stat("Cost Basis", portfolioSummary.totalCostBasis.toUsdFormat()),
            Stat(
                "Total P/L",
                "${portfolioSummary.totalProfitLoss.toUsdFormat()}\n" +
                        "(${portfolioSummary.totalProfitLossPercent.toPercentageFormat()})"
            ),
            Stat(
                "24h Change",
                "${portfolioSummary.totalProfitLoss24h.toUsdFormat()}\n" +
                        "(${portfolioSummary.totalProfitLossPercent24h.toPercentageFormat()})"
            ),
            Stat("Coins Owned", portfolioSummary.coinsOwned.toString())
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Summary",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.titleLarge,
        )
        StatsGrid(stats = stats, modifier = modifier)
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PortfolioSummarySectionPreview() {
    HodlerTheme {
        PortfolioSummarySection(
            PortfolioSummary(
                coinsOwned = 5,
                totalValue = 56165.11,
                totalCostBasis = 47112.87,
                totalProfitLoss = 9052.24,
                totalProfitLossPercent = 119.21,
                totalProfitLoss24h = 751.47,
                totalProfitLossPercent24h = 1.35
            )
        )
    }
}
