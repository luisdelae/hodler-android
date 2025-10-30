package com.luisd.hodler.presentation.ui.details.components

import com.luisd.hodler.domain.model.CoinDetail
import com.luisd.hodler.domain.model.MarketChart
import com.luisd.hodler.domain.model.PricePoint

/**
 * Helper Functions for Coin Detail Screen sample data
 */

fun getSampleBitcoinDetail(): CoinDetail {
    return CoinDetail(
        id = "bitcoin",
        symbol = "btc",
        name = "Bitcoin",
        image = "https://assets.coingecko.com/coins/images/1/large/bitcoin.png",
        currentPrice = 67234.56,
        marketCapUsd = 1_316_789_234_567.89,
        marketCapRank = 1,
        totalVolumeUsd = 28_456_789_123.45,
        priceChangePercentage24h = 3.45,
        circulatingSupply = 19_567_234.0,
        totalSupply = 21_000_000.0,
        maxSupply = 21_000_000.0,
        allTimeHighUsd = 69_045.00,
        allTimeLowUsd = 67.81,
        allTimeHighUsdDate = "2021-11-10T14:24:11.849Z",
        allTimeLowUsdDate = "2013-07-06T00:00:00.000Z"
    )
}

fun getSampleEthereumDetail(): CoinDetail {
    return CoinDetail(
        id = "ethereum",
        symbol = "eth",
        name = "Ethereum",
        image = "https://assets.coingecko.com/coins/images/279/large/ethereum.png",
        currentPrice = 3_456.78,
        marketCapUsd = 415_678_901_234.56,
        marketCapRank = 2,
        totalVolumeUsd = 15_234_567_890.12,
        priceChangePercentage24h = 5.67,
        circulatingSupply = 120_234_567.0,
        totalSupply = 120_234_567.0,
        maxSupply = null,
        allTimeHighUsd = 4_878.26,
        allTimeLowUsd = 0.432,
        allTimeHighUsdDate = "2021-11-10T14:24:19.604Z",
        allTimeLowUsdDate = "2015-10-20T00:00:00.000Z"
    )
}

fun getSampleCoinWithNegativeChange(): CoinDetail {
    return CoinDetail(
        id = "cardano",
        symbol = "ada",
        name = "Cardano",
        image = "https://assets.coingecko.com/coins/images/975/large/cardano.png",
        currentPrice = 0.456,
        marketCapUsd = 16_234_567_890.12,
        marketCapRank = 8,
        totalVolumeUsd = 456_789_012.34,
        priceChangePercentage24h = -4.32,
        circulatingSupply = 35_678_901_234.0,
        totalSupply = 45_000_000_000.0,
        maxSupply = 45_000_000_000.0,
        allTimeHighUsd = 3.09,
        allTimeLowUsd = 0.017,
        allTimeHighUsdDate = "2021-09-02T06:00:10.474Z",
        allTimeLowUsdDate = "2017-10-01T00:00:00.000Z"
    )
}

fun getSampleCoinWithNullSupply(): CoinDetail {
    return CoinDetail(
        id = "dogecoin",
        symbol = "doge",
        name = "Dogecoin",
        image = "https://assets.coingecko.com/coins/images/5/large/dogecoin.png",
        currentPrice = 0.123,
        marketCapUsd = 17_456_789_012.34,
        marketCapRank = 9,
        totalVolumeUsd = 890_123_456.78,
        priceChangePercentage24h = 1.23,
        circulatingSupply = 141_789_012_345.0,
        totalSupply = null,
        maxSupply = null,
        allTimeHighUsd = 0.731,
        allTimeLowUsd = 0.00008547,
        allTimeHighUsdDate = "2021-05-08T05:08:23.458Z",
        allTimeLowUsdDate = "2015-05-06T00:00:00.000Z"
    )
}

fun getSampleMarketChart24H(): MarketChart {
    val now = System.currentTimeMillis()
    val basePrice = 67000.0
    val prices = (0..24).map { hour ->
        PricePoint(
            timestamp = now - (24 - hour) * 3600000L,
            price = basePrice + (Math.random() - 0.5) * 2000
        )
    }
    return MarketChart(prices)
}
