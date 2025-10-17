package com.luisd.hodler.presentation.ui.market

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.luisd.hodler.domain.model.Coin
import com.luisd.hodler.domain.model.Result
import com.luisd.hodler.presentation.theme.HodlerTheme
import com.luisd.hodler.presentation.theme.getProfitLossColor
import com.luisd.hodler.presentation.ui.components.ErrorContent
import com.luisd.hodler.presentation.ui.components.LoadingContent
import com.luisd.hodler.presentation.ui.util.toUsdFormat

@Composable
fun MarketRoute(
    viewModel: MarketViewModel = hiltViewModel(),
    onCoinClick: (String, String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MarketScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onCoinClick = onCoinClick
    )
}

@Composable
fun MarketScreen(
    state: Result<List<Coin>>,
    onRefresh: () -> Unit,
    onCoinClick: (String, String) -> Unit,
) {
    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomBar() }
    ) { paddingValues ->
        when (state) {
            is Result.Loading -> {
                LoadingContent(
                    message = "Gathering coins...",
                    paddingValues = paddingValues
                )
            }

            is Result.Error -> {
                ErrorContent(
                    message = "Failed to load coins",
                    paddingValues = paddingValues,
                    onRefresh = onRefresh,
                )
            }

            is Result.Success -> {
                CoinList(
                    modifier = Modifier.padding(paddingValues),
                    coins = state.data,
                    onCoinClick = onCoinClick,
                )
            }
        }
    }
}

@Composable
fun CoinList(
    modifier: Modifier,
    coins: List<Coin>,
    onCoinClick: (String, String) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .padding(16.dp)
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

// TODO: Placeholder at the moment.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = { Text(text = "Market") },
        actions = {
            IconButton(
                onClick = { }
            ) {
                Icon(Icons.Filled.Search, contentDescription = "Search")
            }
            IconButton(
                onClick = { }
            ) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings")
            }
        }
    )
}


// TODO: Placeholder at the moment.
@Composable
fun BottomBar() {
    NavigationBar(
        modifier = Modifier.height(80.dp)
    ) {
        NavigationBarItem(
            selected = true,
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home icon",
                )
            },
            label = { Text(text = "Home") },
            onClick = { },
        )
        NavigationBarItem(
            selected = false,
            icon = {
                Icon(
                    imageVector = Icons.Filled.BarChart,
                    contentDescription = "Assets icon",
                )
            },
            label = { Text(text = "Assets") },
            onClick = { },
        )
        NavigationBarItem(
            selected = false,
            icon = {
                Icon(
                    imageVector = Icons.Filled.ShoppingBag,
                    contentDescription = "Portfolio icon",
                )
            },
            label = { Text(text = "Portfolio") },
            onClick = { },
        )
    }
}
