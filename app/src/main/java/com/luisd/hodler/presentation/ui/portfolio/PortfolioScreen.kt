package com.luisd.hodler.presentation.ui.portfolio

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.luisd.hodler.presentation.ui.components.ErrorContent
import com.luisd.hodler.presentation.ui.components.LoadingContent
import com.luisd.hodler.presentation.ui.portfolio.components.PortfolioEmptySection
import com.luisd.hodler.presentation.ui.portfolio.components.PortfolioSection

@Composable
fun PortfolioRoute(
    outerPaddingValues: PaddingValues,
    onAddHoldingClick: () -> Unit,
    onEditHolding: (Long) -> Unit,
    onNavigateToCoinDetail: (String, String) -> Unit,
    viewModel: PortfolioViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    PortfolioScreen(
        outerPaddingValues = outerPaddingValues,
        state = state,
        onRefresh = { viewModel.refreshPrices() },
        onAddHoldingClick = onAddHoldingClick,
        onEditHolding = onEditHolding,
        onDeleteHolding = viewModel::deleteHolding,
        onNavigateToCoinDetail = onNavigateToCoinDetail,
    )
}

@Composable
fun PortfolioScreen(
    outerPaddingValues: PaddingValues,
    state: PortfolioUiState,
    onRefresh: () -> Unit,
    onAddHoldingClick: () -> Unit,
    onEditHolding: (Long) -> Unit,
    onDeleteHolding: (Long) -> Unit,
    onNavigateToCoinDetail: (String, String) -> Unit,
) {
    Scaffold(
        topBar = { TopBar() },
        floatingActionButton = {
            if (state is PortfolioUiState.Success) {
                FloatingActionButton(
                    onClick = { onAddHoldingClick() },
                    modifier = Modifier
                        .padding(bottom = outerPaddingValues.calculateBottomPadding())
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add holding",
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(outerPaddingValues)
        ) {
            when (state) {
                is PortfolioUiState.Loading -> {
                    LoadingContent(
                        message = "Loading portfolio...",
                        paddingValues = paddingValues
                    )
                }

                is PortfolioUiState.Error -> {
                    ErrorContent(
                        message = "Failed to load portfolio",
                        paddingValues = paddingValues,
                        onRefresh = onRefresh,
                    )
                }

                is PortfolioUiState.Empty -> {
                    PortfolioEmptySection(
                        onAddNewHolding = onAddHoldingClick,
                        modifier = Modifier.padding(paddingValues)
                    )
                }

                is PortfolioUiState.Success -> {
                    PortfolioSection(
                        state = state,
                        modifier = Modifier.padding(paddingValues),
                        onNavigateToCoinDetail = onNavigateToCoinDetail,
                        onEditHolding = onEditHolding,
                        onDeleteHolding = onDeleteHolding,
                    )
                }
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
