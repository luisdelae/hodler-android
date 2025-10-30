package com.luisd.hodler.presentation.ui.market.components

import com.luisd.hodler.domain.model.Coin

/**
 * Helper Functions for Market Screen sample data
 */

fun getSampleCoins(): List<Coin> {
    return listOf(
        Coin(
            id = "bitcoin",
            symbol = "BTC",
            name = "Bitcoin",
            image = "",
            currentPrice = 100.0,
            priceChangePercentage24h = 54.4,
            marketCap = 55464,
            marketCapRank = 5,
        ),
        Coin(
            id = "ethereum",
            symbol = "ETH",
            name = "Ethereum",
            image = "",
            currentPrice = 100.0,
            priceChangePercentage24h = 54.4,
            marketCap = 55464,
            marketCapRank = 5,
        ),
        Coin(
            id = "cardano",
            symbol = "ADA",
            name = "Cardano",
            image = "",
            currentPrice = 100.0,
            priceChangePercentage24h = 54.4,
            marketCap = 55464,
            marketCapRank = 5,
        ),
        Coin(
            id = "solana",
            symbol = "SOL",
            name = "Solana",
            image = "",
            currentPrice = 100.0,
            priceChangePercentage24h = 54.4,
            marketCap = 55464,
            marketCapRank = 5,
        ),
        Coin(
            id = "polkadot",
            symbol = "DOT",
            name = "Polkadot",
            image = "",
            currentPrice = 100.0,
            priceChangePercentage24h = 54.4,
            marketCap = 55464,
            marketCapRank = 5,
        )
    )
}