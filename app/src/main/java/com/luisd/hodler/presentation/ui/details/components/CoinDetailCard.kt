package com.luisd.hodler.presentation.ui.details.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.luisd.hodler.domain.model.CoinDetail
import com.luisd.hodler.presentation.theme.HodlerTheme
import com.luisd.hodler.presentation.ui.components.ProfitLossPercentText
import com.luisd.hodler.presentation.ui.util.toUsdFormat

@Composable
fun CoinDetailCard(
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
                ProfitLossPercentText(coinDetails.priceChangePercentage24h)
            }

            Surface(
                tonalElevation = 8.dp,
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
fun CoinDetailCardPreview() {
    HodlerTheme {
        CoinDetailCard(
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
                maxSupply = null,
            )
        )
    }
}