package com.luisd.hodler.presentation.ui.details.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.luisd.hodler.domain.model.CoinDetail
import com.luisd.hodler.presentation.theme.HodlerTheme
import com.luisd.hodler.presentation.ui.components.Stat
import com.luisd.hodler.presentation.ui.components.StatsGrid
import com.luisd.hodler.presentation.ui.util.toCompactFormat
import com.luisd.hodler.presentation.ui.util.toUsdFormat

@Composable
fun CoinDetailStatsSection(
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

    StatsGrid(stats = stats, modifier = Modifier.padding(horizontal = 16.dp))
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CoinDetailsStatsGridPreview() {
    HodlerTheme {
        CoinDetailStatsSection(
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