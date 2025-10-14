package com.luisd.hodler.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.luisd.hodler.presentation.ui.market.MarketRoute

@Composable
fun HodlerNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Market
    ) {
        composable<Screen.Market> {
            MarketRoute()
        }
    }
}