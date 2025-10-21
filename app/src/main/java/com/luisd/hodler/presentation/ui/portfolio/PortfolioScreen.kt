package com.luisd.hodler.presentation.ui.portfolio

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.luisd.hodler.domain.model.HoldingWithPrice
import com.luisd.hodler.presentation.ui.components.ErrorContent
import com.luisd.hodler.presentation.ui.components.LoadingContent
import com.luisd.hodler.domain.model.Result

@Composable
fun PortfolioRoute(
    viewModel: PortfolioViewModel = hiltViewModel(),
    onAddHoldingClick: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
}

@Composable
fun PortfolioScreen(
    state: Result<List<HoldingWithPrice>>,
    onRefresh: () -> Unit,
    onAddHoldingClick: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        topBar = { TopBar(onNavigateBack) },
        bottomBar = { BottomBar() },
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
fun TopBar(
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = { Text(text = "Portfolio") },
        navigationIcon = {
            TextButton(onClick = { onNavigateBack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                )
                Text(text = "Back")
            }
        },
    )
}

// TODO: Placeholder at the moment.
@Composable
fun BottomBar() {
    NavigationBar(
        modifier = Modifier.height(80.dp)
    ) {
        NavigationBarItem(
            selected = false,
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
            selected = true,
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