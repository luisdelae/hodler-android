package com.luisd.hodler.presentation.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.luisd.hodler.presentation.ui.components.ErrorContent
import com.luisd.hodler.presentation.ui.components.LoadingContent
import com.luisd.hodler.presentation.ui.details.components.CoinDetailCard
import com.luisd.hodler.presentation.ui.details.components.CoinDetailChartSection
import com.luisd.hodler.presentation.ui.details.components.CoinDetailStatsSection
import com.luisd.hodler.presentation.ui.details.components.TimeRangeChips

@Composable
fun CoinDetailRoute(
    onNavigateBack: () -> Unit,
    onAddToPortfolio: (String) -> Unit,
    viewModel: CoinDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onTimeRangeChange = remember {
        { timeRange: TimeRange -> viewModel.updateTimeRange(timeRange) }
    }

    DetailScreen(
        state = state,
        coinSymbol = viewModel.coinSymbol,
        onRefresh = viewModel::refresh,
        onNavigateBack = onNavigateBack,
        onSelectedTimeRangeChange = { timeRange -> onTimeRangeChange(timeRange) },
        onAddToPortfolio = onAddToPortfolio
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    state: CoinDetailUiState,
    coinSymbol: String,
    onRefresh: () -> Unit,
    onNavigateBack: () -> Unit,
    onSelectedTimeRangeChange: (TimeRange) -> Unit,
    onAddToPortfolio: (String) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = coinSymbol.uppercase()) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        }
    ) { paddingValues ->
        when (state) {
            is CoinDetailUiState.Error -> {
                ErrorContent(
                    message = state.message,
                    paddingValues = paddingValues,
                    onRefresh = { onRefresh() },
                )
            }

            CoinDetailUiState.Loading -> {
                LoadingContent(
                    message = "Loading details...",
                    paddingValues = paddingValues,
                )
            }

            is CoinDetailUiState.Success -> {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CoinDetailCard(coinDetails = state.coinDetail)

                    TimeRangeChips(
                        timeRange = state.timeRange,
                        onSelectedTimeRangeChange = onSelectedTimeRangeChange
                    )

                    CoinDetailChartSection(
                        state = state.chartState,
                        paddingValues = paddingValues,
                        timeRange = state.timeRange
                    )

                    CoinDetailStatsSection(coinDetail = state.coinDetail)

                    TextButton(
                        onClick = { onAddToPortfolio(state.coinDetail.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = "Add to portfolio",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }
        }
    }
}
