package com.luisd.hodler.presentation.ui.market.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.luisd.hodler.domain.model.Coin
import com.luisd.hodler.presentation.theme.HodlerTheme
import com.luisd.hodler.presentation.theme.getProfitLossColor
import com.luisd.hodler.presentation.ui.util.toUsdFormat

@Composable
fun CoinList(
    modifier: Modifier,
    coins: List<Coin>,
    onCoinClick: (String, String) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 0.dp)
            .fillMaxSize()
    ) {
        items(coins) { coin ->
            CoinListItem(
                coin = coin,
                onClick = { onCoinClick(coin.id, coin.symbol) })
        }
    }
}

@Composable
fun CoinListItem(
    coin: Coin,
    onClick: () -> Unit = { }
) {
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(coin.image)
                    .crossfade(true)
                    .build(),
                contentDescription = "${coin.name} logo",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = coin.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = coin.symbol.uppercase(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.semantics(mergeDescendants = true) {
                    contentDescription = "${coin.name} price ${coin.currentPrice.toUsdFormat()}, " +
                            "change ${coin.priceChangePercentage24h}"
                }
            ) {
                Text(
                    text = coin.currentPrice.toUsdFormat(),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "${coin.priceChangePercentage24h}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = getProfitLossColor(coin.priceChangePercentage24h)
                )
            }
        }
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CoinListItemPreview() {
    HodlerTheme {
        CoinListItem(
            coin = Coin(
                id = "bitcoin",
                name = "Bitcoin",
                symbol = "btc",
                image = "",
                currentPrice = 43250.50,
                priceChangePercentage24h = 2.5,
                marketCap = 845000000000,
                marketCapRank = 1,
            ),
            onClick = { },
        )
    }
}