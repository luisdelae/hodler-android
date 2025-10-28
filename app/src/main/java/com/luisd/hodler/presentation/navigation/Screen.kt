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
    @Serializable
    data object Portfolio : Screen

    @Serializable
    data class AddHoldingScreen(
        val holdingId: Long? = null,
        val coinId: String? = null,
    ) : Screen
}
