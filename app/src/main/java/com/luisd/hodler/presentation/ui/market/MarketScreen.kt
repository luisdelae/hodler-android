package com.luisd.hodler.presentation.ui.market

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.luisd.hodler.domain.model.Coin
import com.luisd.hodler.domain.model.Result


@Composable
fun MarketRoute(
    viewModel: MarketViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MarketScreen(
        state = state,
        onRefresh = viewModel::refresh
    )
}

@Composable
fun MarketScreen(
    state: Result<List<Coin>>,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        is Result.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is Result.Error -> {

        }

        is Result.Success -> {
            CoinList(state.data)
        }
    }
}

@Composable
fun CoinList(
    coins: List<Coin>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(coins) { coin ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = coin.name)
            }
        }
    }
}