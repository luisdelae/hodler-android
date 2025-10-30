package com.luisd.hodler.presentation.ui.market

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.luisd.hodler.presentation.theme.HodlerTheme
import com.luisd.hodler.presentation.ui.components.CacheIndicatorBanner
import com.luisd.hodler.presentation.ui.components.ErrorContent
import com.luisd.hodler.presentation.ui.components.LoadingContent
import com.luisd.hodler.presentation.ui.market.components.CoinList
import com.luisd.hodler.presentation.ui.market.components.getSampleCoins

@Composable
fun MarketRoute(
    outerPaddingValues: PaddingValues,
    onCoinClick: (String, String) -> Unit,
    viewModel: MarketViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MarketScreen(
        outerPaddingValues = outerPaddingValues,
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onSearchActiveChange = viewModel::onSearchActiveChange,
        onRefresh = viewModel::refresh,
        onCoinClick = onCoinClick
    )
}

@Composable
fun MarketScreen(
    outerPaddingValues: PaddingValues,
    uiState: MarketUiState,
    onSearchQueryChange: (String) -> Unit,
    onSearchActiveChange: (Boolean) -> Unit,
    onRefresh: () -> Unit,
    onCoinClick: (String, String) -> Unit,
) {
    val isRefreshing = (uiState as? MarketUiState.Success)?.isRefreshing ?: false

    Scaffold(
        topBar = {
            Column {
                TopBar(
                    searchQuery = (uiState as? MarketUiState.Success)?.searchQuery ?: "",
                    isSearchActive = (uiState as? MarketUiState.Success)?.isSearchActive ?: false,
                    onSearchQueryChange = onSearchQueryChange,
                    onSearchActiveChange = onSearchActiveChange
                )

                if (uiState is MarketUiState.Success && uiState.isFromCache) {
                    CacheIndicatorBanner(
                        lastUpdated = uiState.lastUpdated,
                        onRefresh = onRefresh
                    )
                }
            }
        },
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.padding(outerPaddingValues)
        ) {
            when (uiState) {
                is MarketUiState.Loading -> {
                    LoadingContent(
                        message = "Gathering coins...",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    )
                }

                is MarketUiState.Error -> {
                    ErrorContent(
                        message = uiState.message,
                        onRefresh = onRefresh,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    )
                }

                is MarketUiState.Success -> {
                    val displayedCoins = uiState.displayedCoins

                    if (displayedCoins.isEmpty() && uiState.searchQuery.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(paddingValues)
                                .padding(16.dp)
                        ) {
                            Text("No coins found for \"${uiState.searchQuery}\"")
                        }
                    } else {
                        CoinList(
                            modifier = Modifier.padding(paddingValues),
                            coins = displayedCoins,
                            onCoinClick = onCoinClick,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    searchQuery: String,
    isSearchActive: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSearchActiveChange: (Boolean) -> Unit
) {
    TopAppBar(
        title = {
            if (isSearchActive) {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search coins...") },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(text = "Market")
            }
        },
        navigationIcon = {
            if (isSearchActive) {
                IconButton(onClick = {
                    onSearchActiveChange(false)
                    onSearchQueryChange("")
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close search")
                }
            }
        },
        actions = {
            if (isSearchActive && searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear search")
                }
            } else if (!isSearchActive) {
                IconButton(onClick = { onSearchActiveChange(true) }) {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                }
            }
        }
    )
}

// ============================================================
// MarketScreen Previews
// ============================================================

@Preview(name = "Light: Market - Loading State", showBackground = true)
@Preview(name = "Dark: Market - Loading State", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewMarketScreenLoading() {
    HodlerTheme {
        MarketScreen(
            outerPaddingValues = PaddingValues(0.dp),
            uiState = MarketUiState.Loading,
            onSearchQueryChange = {},
            onSearchActiveChange = {},
            onRefresh = {},
            onCoinClick = { _, _ -> }
        )
    }
}

@Preview(name = "Light: Market - Error State", showBackground = true)
@Preview(name = "Dark: Market - Error State", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewMarketScreenError() {
    HodlerTheme {
        MarketScreen(
            outerPaddingValues = PaddingValues(0.dp),
            uiState = MarketUiState.Error("Network connection failed. Please try again."),
            onSearchQueryChange = {},
            onSearchActiveChange = {},
            onRefresh = {},
            onCoinClick = { _, _ -> }
        )
    }
}

@Preview(name = "Light: Market - Success State", showBackground = true)
@Preview(name = "Dark: Market - Success State", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewMarketScreenSuccess() {
    HodlerTheme {
        MarketScreen(
            outerPaddingValues = PaddingValues(0.dp),
            uiState = MarketUiState.Success(
                coins = getSampleCoins(),
                searchQuery = "",
                isSearchActive = false,
                isRefreshing = false
            ),
            onSearchQueryChange = {},
            onSearchActiveChange = {},
            onRefresh = {},
            onCoinClick = { _, _ -> }
        )
    }
}

@Preview(name = "Light: Market - Success State with cache", showBackground = true)
@Preview(name = "Dark: Market - Success State with cache", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewMarketScreenSuccess_Cache() {
    HodlerTheme {
        MarketScreen(
            outerPaddingValues = PaddingValues(0.dp),
            uiState = MarketUiState.Success(
                coins = getSampleCoins(),
                searchQuery = "",
                isSearchActive = false,
                isRefreshing = false,
                isFromCache = true,
            ),
            onSearchQueryChange = {},
            onSearchActiveChange = {},
            onRefresh = {},
            onCoinClick = { _, _ -> }
        )
    }
}
