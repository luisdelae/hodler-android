package com.luisd.hodler.presentation.ui.details.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.luisd.hodler.domain.model.CoinDetail
import com.luisd.hodler.presentation.theme.HodlerTheme
import com.luisd.hodler.presentation.ui.util.toCompactFormat
import com.luisd.hodler.presentation.ui.util.toUsdFormat

data class Stat(val label: String, val value: String)

@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
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
    coinDetail: CoinDetail,
) {
    val stats = remember(coinDetail) {
        listOf(
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
    }


    FlowRow(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 2,
    ) {
        val itemModifier = Modifier.weight(1f)

        stats.forEach { stat ->
            StatCard(
                label = stat.label,
                value = stat.value,
                modifier = itemModifier,
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
                maxSupply = null,
            )
        )
    }
}
