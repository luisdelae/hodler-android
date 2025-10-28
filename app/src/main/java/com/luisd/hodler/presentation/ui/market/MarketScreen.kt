package com.luisd.hodler.presentation.ui.market

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearchActive by viewModel.isSearchActive.collectAsStateWithLifecycle()

    MarketScreen(
        outerPaddingValues = outerPaddingValues,
        state = state,
        searchQuery = searchQuery,
        isSearchActive = isSearchActive,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onSearchActiveChange = viewModel::onSearchActiveChange,
        onRefresh = viewModel::refresh,
        onCoinClick = onCoinClick
    )
}

@Composable
fun MarketScreen(
    outerPaddingValues: PaddingValues,
    state: Result<List<Coin>>,
    searchQuery: String,
    isSearchActive: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSearchActiveChange: (Boolean) -> Unit,
    onRefresh: () -> Unit,
    onCoinClick: (String, String) -> Unit,
) {
    Scaffold(
        topBar = {
            TopBar(
                searchQuery = searchQuery,
                isSearchActive = isSearchActive,
                onSearchQueryChange = onSearchQueryChange,
                onSearchActiveChange = onSearchActiveChange
            )
        },
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
                    if (state.data.isEmpty() && searchQuery.isNotBlank()) {
                        // Empty search results
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(paddingValues)
                                .padding(16.dp)
                        ) {
                            Text("No coins found for \"$searchQuery\"")
                        }
                    } else {
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
