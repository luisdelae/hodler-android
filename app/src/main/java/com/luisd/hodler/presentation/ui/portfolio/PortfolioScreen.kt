package com.luisd.hodler.presentation.ui.portfolio

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.luisd.hodler.domain.model.Result
import com.luisd.hodler.presentation.ui.components.ErrorContent
import com.luisd.hodler.presentation.ui.components.LoadingContent

@Composable
fun PortfolioRoute(
    outerPaddingValues: PaddingValues,
    onAddHoldingClick: () -> Unit,
    viewModel: PortfolioViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PortfolioScreen(
        outerPaddingValues = outerPaddingValues,
        state = state,
        onRefresh = { viewModel.refresh() },
        onAddHoldingClick = onAddHoldingClick
    )
}

@Composable
fun PortfolioScreen(
    outerPaddingValues: PaddingValues,
    state: Result<Portfolio>,
    onRefresh: () -> Unit,
    onAddHoldingClick: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.padding(outerPaddingValues),
        topBar = { TopBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddHoldingClick() }
            ) {

            }
        }
    ) { paddingValues ->
        when (state) {
            is Result.Loading -> {
                LoadingContent(
                    message = "Loading portfolio...",
                    paddingValues = paddingValues
                )
            }

            is Result.Error -> {
                ErrorContent(
                    message = "Failed to load portfolio",
                    paddingValues = paddingValues,
                    onRefresh = onRefresh,
                )
            }

            is Result.Success -> {

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = { Text(text = "Portfolio") },
    )
}
