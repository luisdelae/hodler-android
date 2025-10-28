package com.luisd.hodler.presentation.ui.market

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.luisd.hodler.domain.model.Coin
import com.luisd.hodler.domain.model.Result
import com.luisd.hodler.presentation.ui.components.ErrorContent
import com.luisd.hodler.presentation.ui.components.LoadingContent
import com.luisd.hodler.presentation.ui.market.components.CoinList

@Composable
fun MarketRoute(
    outerPaddingValues: PaddingValues,
    onCoinClick: (String, String) -> Unit,
    viewModel: MarketViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MarketScreen(
        outerPaddingValues = outerPaddingValues,
        state = state,
        onRefresh = viewModel::refresh,
        onCoinClick = onCoinClick
    )
}

@Composable
fun MarketScreen(
    outerPaddingValues: PaddingValues,
    state: Result<List<Coin>>,
    onRefresh: () -> Unit,
    onCoinClick: (String, String) -> Unit,
) {
    Scaffold(
        topBar = { TopBar() },
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(outerPaddingValues)
        ) {
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
