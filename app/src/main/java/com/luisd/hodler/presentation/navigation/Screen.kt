package com.luisd.hodler.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Market : Screen
    @Serializable
    data class CoinDetail(
        val coinId: String,
        val coinSymbol: String,
    ) : Screen
}