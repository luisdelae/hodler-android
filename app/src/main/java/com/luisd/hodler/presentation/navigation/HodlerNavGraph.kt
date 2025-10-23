package com.luisd.hodler.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.luisd.hodler.presentation.ui.details.CoinDetailRoute
import com.luisd.hodler.presentation.ui.market.MarketRoute
import com.luisd.hodler.presentation.ui.portfolio.PortfolioRoute

@Composable
fun HodlerNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    HodlerScaffold(
        navController = navController,
        modifier = modifier,
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Market
        ) {
            composable<Screen.Market> {
                MarketRoute(
                    outerPaddingValues = paddingValues,
                    onCoinClick = { coinId, coinSymbol ->
                        navController.navigate(
                            Screen.CoinDetail(coinId = coinId, coinSymbol = coinSymbol)
                        )
                    }
                )
            }
            composable<Screen.CoinDetail> { backStackEntry ->
                CoinDetailRoute(onNavigateBack = { navController.popBackStack() })
            }
            composable<Screen.Portfolio> { backStackEntry ->
                PortfolioRoute(
                    outerPaddingValues = paddingValues,
                    onAddHoldingClick = {
                        // TODO: Add when holding screen is created
                    },
                    onNavigateToCoinDetail = { coinId, symbol ->
                        Screen.CoinDetail(coinId = coinId, coinSymbol = symbol)
                    }
                )
            }
        }
    }
}
