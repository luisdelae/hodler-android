package com.luisd.hodler.presentation.ui.holdings.components

import com.luisd.hodler.domain.model.Coin

/**
 * Helper Functions for Add Holding Screen sample data
 */

fun getSampleCoinsForSelection(): List<Coin> {
    return listOf(
        Coin(
            id = "bitcoin",
            symbol = "BTC",
            name = "Bitcoin",
            image = "",
            currentPrice = 54231.12,
            priceChangePercentage24h = 2.5,
            marketCap = 1000000000L,
            marketCapRank = 1
        ),
        Coin(
            id = "ethereum",
            symbol = "ETH",
            name = "Ethereum",
            image = "",
            currentPrice = 4012.58,
            priceChangePercentage24h = -1.5,
            marketCap = 500000000L,
            marketCapRank = 2
        ),
        Coin(
            id = "cardano",
            symbol = "ADA",
            name = "Cardano",
            image = "",
            currentPrice = 0.52,
            priceChangePercentage24h = 3.2,
            marketCap = 18000000L,
            marketCapRank = 8
        )
    )
}

fun getSampleBitcoinForForm(): Coin {
    return Coin(
        id = "bitcoin",
        symbol = "BTC",
        name = "Bitcoin",
        image = "",
        currentPrice = 54231.12,
        priceChangePercentage24h = 2.5,
        marketCap = 1000000000L,
        marketCapRank = 1
    )
}

fun getSampleEthereumForForm(): Coin {
    return Coin(
        id = "ethereum",
        symbol = "ETH",
        name = "Ethereum",
        image = "",
        currentPrice = 4012.58,
        priceChangePercentage24h = -1.5,
        marketCap = 500000000L,
        marketCapRank = 2
    )
}