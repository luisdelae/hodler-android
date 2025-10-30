package com.luisd.hodler.presentation.ui.portfolio.components

import com.luisd.hodler.domain.model.Holding
import com.luisd.hodler.domain.model.HoldingWithPrice
import com.luisd.hodler.domain.model.PortfolioSummary
import com.luisd.hodler.presentation.ui.portfolio.CoinGroup

/**
 * Helper Functions for Portfolio Screen sample data
 */

fun getSamplePortfolioSummaryWithProfits(): PortfolioSummary {
    return PortfolioSummary(
        coinsOwned = 3,
        totalValue = 95234.56,
        totalCostBasis = 75000.0,
        totalProfitLoss = 20234.56,
        totalProfitLossPercent = 26.98,
        totalProfitLoss24h = 3210.45,
        totalProfitLossPercent24h = 3.49
    )
}

fun getSamplePortfolioSummaryWithLosses(): PortfolioSummary {
    return PortfolioSummary(
        coinsOwned = 3,
        totalValue = 62000.0,
        totalCostBasis = 75000.0,
        totalProfitLoss = -13000.0,
        totalProfitLossPercent = -17.33,
        totalProfitLoss24h = -2500.0,
        totalProfitLossPercent24h = -3.88
    )
}

fun getSampleBitcoinGroup(): CoinGroup {
    val holding = Holding(
        id = 1L,
        coinId = "bitcoin",
        coinSymbol = "BTC",
        coinName = "Bitcoin",
        amount = 1.0,
        purchasePrice = 50000.0,
        purchaseDate = System.currentTimeMillis() - 90 * 86400000L, // 90 days ago
        imageUrl = "https://assets.coingecko.com/coins/images/1/large/bitcoin.png"
    )

    val holdingWithPrice = HoldingWithPrice(
        holding = holding,
        currentPrice = 67234.56,
        currentValue = 67234.56,
        costBasis = 50000.0,
        profitLoss = 17234.56,
        profitLossPercent = 34.47,
        profitLoss24h = 2310.45,
        profitLossPercent24h = 3.56
    )

    return CoinGroup(
        coinId = "bitcoin",
        coinSymbol = "BTC",
        coinName = "Bitcoin",
        imageUrl = "https://assets.coingecko.com/coins/images/1/large/bitcoin.png",
        totalAmount = 1.0,
        averageCostBasis = 50000.0,
        totalCurrentValue = 67234.56,
        totalProfitLoss = 17234.56,
        totalProfitLossPercent = 34.47,
        holdings = listOf(holdingWithPrice)
    )
}

fun getSampleEthereumGroup(): CoinGroup {
    val holding = Holding(
        id = 2L,
        coinId = "ethereum",
        coinSymbol = "ETH",
        coinName = "Ethereum",
        amount = 5.0,
        purchasePrice = 3000.0,
        purchaseDate = System.currentTimeMillis() - 60 * 86400000L,
        imageUrl = "https://assets.coingecko.com/coins/images/279/large/ethereum.png"
    )

    val holdingWithPrice = HoldingWithPrice(
        holding = holding,
        currentPrice = 3456.78,
        currentValue = 17283.90,
        costBasis = 15000.0,
        profitLoss = 2283.90,
        profitLossPercent = 15.23,
        profitLoss24h = 567.89,
        profitLossPercent24h = 3.40
    )

    return CoinGroup(
        coinId = "ethereum",
        coinSymbol = "ETH",
        coinName = "Ethereum",
        imageUrl = "https://assets.coingecko.com/coins/images/279/large/ethereum.png",
        totalAmount = 5.0,
        averageCostBasis = 3000.0,
        totalCurrentValue = 17283.90,
        totalProfitLoss = 2283.90,
        totalProfitLossPercent = 15.23,
        holdings = listOf(holdingWithPrice)
    )
}

fun getSampleCardanoGroupWithLoss(): CoinGroup {
    val holding = Holding(
        id = 3L,
        coinId = "cardano",
        coinSymbol = "ADA",
        coinName = "Cardano",
        amount = 20000.0,
        purchasePrice = 0.65,
        purchaseDate = System.currentTimeMillis() - 180 * 86400000L,
        imageUrl = "https://assets.coingecko.com/coins/images/975/large/cardano.png"
    )

    val holdingWithPrice = HoldingWithPrice(
        holding = holding,
        currentPrice = 0.456,
        currentValue = 9120.0,
        costBasis = 13000.0,
        profitLoss = -3880.0,
        profitLossPercent = -29.85,
        profitLoss24h = -394.56,
        profitLossPercent24h = -4.15
    )

    return CoinGroup(
        coinId = "cardano",
        coinSymbol = "ADA",
        coinName = "Cardano",
        imageUrl = "https://assets.coingecko.com/coins/images/975/large/cardano.png",
        totalAmount = 20000.0,
        averageCostBasis = 0.65,
        totalCurrentValue = 9120.0,
        totalProfitLoss = -3880.0,
        totalProfitLossPercent = -29.85,
        holdings = listOf(holdingWithPrice)
    )
}

fun getSampleCoinGroupsWithProfits(): List<CoinGroup> {
    return listOf(
        getSampleBitcoinGroup(),
        getSampleEthereumGroup(),
        CoinGroup(
            coinId = "solana",
            coinSymbol = "SOL",
            coinName = "Solana",
            imageUrl = "https://assets.coingecko.com/coins/images/4128/large/solana.png",
            totalAmount = 50.0,
            averageCostBasis = 100.0,
            totalCurrentValue = 10716.10,
            totalProfitLoss = 5716.10,
            totalProfitLossPercent = 114.32,
            holdings = listOf(
                HoldingWithPrice(
                    holding = Holding(
                        id = 4L,
                        coinId = "solana",
                        coinSymbol = "SOL",
                        coinName = "Solana",
                        amount = 50.0,
                        purchasePrice = 100.0,
                        purchaseDate = System.currentTimeMillis() - 120 * 86400000L,
                        imageUrl = "https://assets.coingecko.com/coins/images/4128/large/solana.png"
                    ),
                    currentPrice = 214.32,
                    currentValue = 10716.10,
                    costBasis = 5000.0,
                    profitLoss = 5716.10,
                    profitLossPercent = 114.32,
                    profitLoss24h = 332.11,
                    profitLossPercent24h = 3.20
                )
            )
        )
    )
}

fun getSampleCoinGroupsWithLosses(): List<CoinGroup> {
    return listOf(
        getSampleCardanoGroupWithLoss(),
        CoinGroup(
            coinId = "ripple",
            coinSymbol = "XRP",
            coinName = "XRP",
            imageUrl = "https://assets.coingecko.com/coins/images/44/large/xrp.png",
            totalAmount = 10000.0,
            averageCostBasis = 0.80,
            totalCurrentValue = 5200.0,
            totalProfitLoss = -2800.0,
            totalProfitLossPercent = -35.0,
            holdings = listOf(
                HoldingWithPrice(
                    holding = Holding(
                        id = 5L,
                        coinId = "ripple",
                        coinSymbol = "XRP",
                        coinName = "XRP",
                        amount = 10000.0,
                        purchasePrice = 0.80,
                        purchaseDate = System.currentTimeMillis() - 200 * 86400000L,
                        imageUrl = "https://assets.coingecko.com/coins/images/44/large/xrp.png"
                    ),
                    currentPrice = 0.52,
                    currentValue = 5200.0,
                    costBasis = 8000.0,
                    profitLoss = -2800.0,
                    profitLossPercent = -35.0,
                    profitLoss24h = -156.0,
                    profitLossPercent24h = -2.91
                )
            )
        ),
        CoinGroup(
            coinId = "polygon",
            coinSymbol = "MATIC",
            coinName = "Polygon",
            imageUrl = "https://assets.coingecko.com/coins/images/4713/large/matic.png",
            totalAmount = 5000.0,
            averageCostBasis = 1.20,
            totalCurrentValue = 4680.0,
            totalProfitLoss = -1320.0,
            totalProfitLossPercent = -22.0,
            holdings = listOf(
                HoldingWithPrice(
                    holding = Holding(
                        id = 6L,
                        coinId = "polygon",
                        coinSymbol = "MATIC",
                        coinName = "Polygon",
                        amount = 5000.0,
                        purchasePrice = 1.20,
                        purchaseDate = System.currentTimeMillis() - 150 * 86400000L,
                        imageUrl = "https://assets.coingecko.com/coins/images/4713/large/matic.png"
                    ),
                    currentPrice = 0.936,
                    currentValue = 4680.0,
                    costBasis = 6000.0,
                    profitLoss = -1320.0,
                    profitLossPercent = -22.0,
                    profitLoss24h = -93.6,
                    profitLossPercent24h = -1.96
                )
            )
        )
    )
}

fun getSampleCoinGroupsWithMultipleHoldings(): List<CoinGroup> {
    val btcHolding1 = HoldingWithPrice(
        holding = Holding(
            id = 1L,
            coinId = "bitcoin",
            coinSymbol = "BTC",
            coinName = "Bitcoin",
            amount = 0.5,
            purchasePrice = 45000.0,
            purchaseDate = System.currentTimeMillis() - 120 * 86400000L,
            imageUrl = "https://assets.coingecko.com/coins/images/1/large/bitcoin.png"
        ),
        currentPrice = 67234.56,
        currentValue = 33617.28,
        costBasis = 22500.0,
        profitLoss = 11117.28,
        profitLossPercent = 49.41,
        profitLoss24h = 1155.22,
        profitLossPercent24h = 3.56
    )

    val btcHolding2 = HoldingWithPrice(
        holding = Holding(
            id = 2L,
            coinId = "bitcoin",
            coinSymbol = "BTC",
            coinName = "Bitcoin",
            amount = 0.3,
            purchasePrice = 55000.0,
            purchaseDate = System.currentTimeMillis() - 60 * 86400000L,
            imageUrl = "https://assets.coingecko.com/coins/images/1/large/bitcoin.png"
        ),
        currentPrice = 67234.56,
        currentValue = 20170.37,
        costBasis = 16500.0,
        profitLoss = 3670.37,
        profitLossPercent = 22.24,
        profitLoss24h = 693.13,
        profitLossPercent24h = 3.56
    )

    val btcHolding3 = HoldingWithPrice(
        holding = Holding(
            id = 3L,
            coinId = "bitcoin",
            coinSymbol = "BTC",
            coinName = "Bitcoin",
            amount = 0.2,
            purchasePrice = 60000.0,
            purchaseDate = System.currentTimeMillis() - 30 * 86400000L,
            imageUrl = "https://assets.coingecko.com/coins/images/1/large/bitcoin.png"
        ),
        currentPrice = 67234.56,
        currentValue = 13446.91,
        costBasis = 12000.0,
        profitLoss = 1446.91,
        profitLossPercent = 12.06,
        profitLoss24h = 462.09,
        profitLossPercent24h = 3.56
    )

    val ethHolding1 = HoldingWithPrice(
        holding = Holding(
            id = 4L,
            coinId = "ethereum",
            coinSymbol = "ETH",
            coinName = "Ethereum",
            amount = 3.0,
            purchasePrice = 2800.0,
            purchaseDate = System.currentTimeMillis() - 90 * 86400000L,
            imageUrl = "https://assets.coingecko.com/coins/images/279/large/ethereum.png"
        ),
        currentPrice = 3456.78,
        currentValue = 10370.34,
        costBasis = 8400.0,
        profitLoss = 1970.34,
        profitLossPercent = 23.46,
        profitLoss24h = 340.34,
        profitLossPercent24h = 3.40
    )

    val ethHolding2 = HoldingWithPrice(
        holding = Holding(
            id = 5L,
            coinId = "ethereum",
            coinSymbol = "ETH",
            coinName = "Ethereum",
            amount = 2.0,
            purchasePrice = 3200.0,
            purchaseDate = System.currentTimeMillis() - 45 * 86400000L,
            imageUrl = "https://assets.coingecko.com/coins/images/279/large/ethereum.png"
        ),
        currentPrice = 3456.78,
        currentValue = 6913.56,
        costBasis = 6400.0,
        profitLoss = 513.56,
        profitLossPercent = 8.02,
        profitLoss24h = 227.55,
        profitLossPercent24h = 3.40
    )

    return listOf(
        CoinGroup(
            coinId = "bitcoin",
            coinSymbol = "BTC",
            coinName = "Bitcoin",
            imageUrl = "https://assets.coingecko.com/coins/images/1/large/bitcoin.png",
            totalAmount = 1.0,
            averageCostBasis = 51000.0,
            totalCurrentValue = 67234.56,
            totalProfitLoss = 16234.56,
            totalProfitLossPercent = 31.83,
            holdings = listOf(btcHolding1, btcHolding2, btcHolding3)
        ),
        CoinGroup(
            coinId = "ethereum",
            coinSymbol = "ETH",
            coinName = "Ethereum",
            imageUrl = "https://assets.coingecko.com/coins/images/279/large/ethereum.png",
            totalAmount = 5.0,
            averageCostBasis = 2960.0,
            totalCurrentValue = 17283.90,
            totalProfitLoss = 2483.90,
            totalProfitLossPercent = 16.78,
            holdings = listOf(ethHolding1, ethHolding2)
        ),
        getSampleCardanoGroupWithLoss()
    )
}

fun getSampleMixedPerformanceCoinGroups(): List<CoinGroup> {
    return listOf(
        getSampleBitcoinGroup(), // Profit
        getSampleCardanoGroupWithLoss(), // Loss
        CoinGroup(
            coinId = "chainlink",
            coinSymbol = "LINK",
            coinName = "Chainlink",
            imageUrl = "https://assets.coingecko.com/coins/images/877/large/chainlink.png",
            totalAmount = 500.0,
            averageCostBasis = 15.0,
            totalCurrentValue = 8716.10,
            totalProfitLoss = 1216.10,
            totalProfitLossPercent = 16.21,
            holdings = listOf(
                HoldingWithPrice(
                    holding = Holding(
                        id = 7L,
                        coinId = "chainlink",
                        coinSymbol = "LINK",
                        coinName = "Chainlink",
                        amount = 500.0,
                        purchasePrice = 15.0,
                        purchaseDate = System.currentTimeMillis() - 100 * 86400000L,
                        imageUrl = "https://assets.coingecko.com/coins/images/877/large/chainlink.png"
                    ),
                    currentPrice = 17.43,
                    currentValue = 8716.10,
                    costBasis = 7500.0,
                    profitLoss = 1216.10,
                    profitLossPercent = 16.21,
                    profitLoss24h = -174.32,
                    profitLossPercent24h = -1.96
                )
            )
        ),
        CoinGroup(
            coinId = "polkadot",
            coinSymbol = "DOT",
            coinName = "Polkadot",
            imageUrl = "https://assets.coingecko.com/coins/images/12171/large/polkadot.png",
            totalAmount = 1000.0,
            averageCostBasis = 8.50,
            totalCurrentValue = 6380.0,
            totalProfitLoss = -1620.0,
            totalProfitLossPercent = -19.06,
            holdings = listOf(
                HoldingWithPrice(
                    holding = Holding(
                        id = 8L,
                        coinId = "polkadot",
                        coinSymbol = "DOT",
                        coinName = "Polkadot",
                        amount = 1000.0,
                        purchasePrice = 8.50,
                        purchaseDate = System.currentTimeMillis() - 140 * 86400000L,
                        imageUrl = "https://assets.coingecko.com/coins/images/12171/large/polkadot.png"
                    ),
                    currentPrice = 6.38,
                    currentValue = 6380.0,
                    costBasis = 8000.0,
                    profitLoss = -1620.0,
                    profitLossPercent = -19.06,
                    profitLoss24h = -127.6,
                    profitLossPercent24h = -1.96
                )
            )
        ),
        CoinGroup(
            coinId = "avalanche",
            coinSymbol = "AVAX",
            coinName = "Avalanche",
            imageUrl = "https://assets.coingecko.com/coins/images/12559/large/avalanche.png",
            totalAmount = 200.0,
            averageCostBasis = 35.0,
            totalCurrentValue = 7804.0,
            totalProfitLoss = 804.0,
            totalProfitLossPercent = 11.49,
            holdings = listOf(
                HoldingWithPrice(
                    holding = Holding(
                        id = 9L,
                        coinId = "avalanche",
                        coinSymbol = "AVAX",
                        coinName = "Avalanche",
                        amount = 200.0,
                        purchasePrice = 35.0,
                        purchaseDate = System.currentTimeMillis() - 75 * 86400000L,
                        imageUrl = "https://assets.coingecko.com/coins/images/12559/large/avalanche.png"
                    ),
                    currentPrice = 39.02,
                    currentValue = 7804.0,
                    costBasis = 7000.0,
                    profitLoss = 804.0,
                    profitLossPercent = 11.49,
                    profitLoss24h = 156.08,
                    profitLossPercent24h = 2.04
                )
            )
        )
    )
}

fun getSampleManyCoinGroups(): List<CoinGroup> {
    return getSampleMixedPerformanceCoinGroups() + listOf(
        CoinGroup(
            coinId = "uniswap",
            coinSymbol = "UNI",
            coinName = "Uniswap",
            imageUrl = "https://assets.coingecko.com/coins/images/12504/large/uniswap.png",
            totalAmount = 300.0,
            averageCostBasis = 12.0,
            totalCurrentValue = 4236.0,
            totalProfitLoss = 636.0,
            totalProfitLossPercent = 17.67,
            holdings = listOf(
                HoldingWithPrice(
                    holding = Holding(
                        id = 10L,
                        coinId = "uniswap",
                        coinSymbol = "UNI",
                        coinName = "Uniswap",
                        amount = 300.0,
                        purchasePrice = 12.0,
                        purchaseDate = System.currentTimeMillis() - 50 * 86400000L,
                        imageUrl = "https://assets.coingecko.com/coins/images/12504/large/uniswap.png"
                    ),
                    currentPrice = 14.12,
                    currentValue = 4236.0,
                    costBasis = 3600.0,
                    profitLoss = 636.0,
                    profitLossPercent = 17.67,
                    profitLoss24h = 84.72,
                    profitLossPercent24h = 2.04
                )
            )
        ),
        CoinGroup(
            coinId = "litecoin",
            coinSymbol = "LTC",
            coinName = "Litecoin",
            imageUrl = "https://assets.coingecko.com/coins/images/2/large/litecoin.png",
            totalAmount = 15.0,
            averageCostBasis = 180.0,
            totalCurrentValue = 1276.50,
            totalProfitLoss = -1423.50,
            totalProfitLossPercent = -52.76,
            holdings = listOf(
                HoldingWithPrice(
                    holding = Holding(
                        id = 11L,
                        coinId = "litecoin",
                        coinSymbol = "LTC",
                        coinName = "Litecoin",
                        amount = 15.0,
                        purchasePrice = 180.0,
                        purchaseDate = System.currentTimeMillis() - 250 * 86400000L,
                        imageUrl = "https://assets.coingecko.com/coins/images/2/large/litecoin.png"
                    ),
                    currentPrice = 85.10,
                    currentValue = 1276.50,
                    costBasis = 2700.0,
                    profitLoss = -1423.50,
                    profitLossPercent = -52.76,
                    profitLoss24h = -38.30,
                    profitLossPercent24h = -2.91
                )
            )
        ),
        CoinGroup(
            coinId = "cosmos",
            coinSymbol = "ATOM",
            coinName = "Cosmos",
            imageUrl = "https://assets.coingecko.com/coins/images/1481/large/cosmos.png",
            totalAmount = 800.0,
            averageCostBasis = 10.0,
            totalCurrentValue = 8896.0,
            totalProfitLoss = 896.0,
            totalProfitLossPercent = 11.20,
            holdings = listOf(
                HoldingWithPrice(
                    holding = Holding(
                        id = 12L,
                        coinId = "cosmos",
                        coinSymbol = "ATOM",
                        coinName = "Cosmos",
                        amount = 800.0,
                        purchasePrice = 10.0,
                        purchaseDate = System.currentTimeMillis() - 110 * 86400000L,
                        imageUrl = "https://assets.coingecko.com/coins/images/1481/large/cosmos.png"
                    ),
                    currentPrice = 11.12,
                    currentValue = 8896.0,
                    costBasis = 8000.0,
                    profitLoss = 896.0,
                    profitLossPercent = 11.20,
                    profitLoss24h = 177.92,
                    profitLossPercent24h = 2.04
                )
            )
        ),
        CoinGroup(
            coinId = "algorand",
            coinSymbol = "ALGO",
            coinName = "Algorand",
            imageUrl = "https://assets.coingecko.com/coins/images/4380/large/algorand.png",
            totalAmount = 5000.0,
            averageCostBasis = 0.50,
            totalCurrentValue = 1820.0,
            totalProfitLoss = -680.0,
            totalProfitLossPercent = -27.20,
            holdings = listOf(
                HoldingWithPrice(
                    holding = Holding(
                        id = 13L,
                        coinId = "algorand",
                        coinSymbol = "ALGO",
                        coinName = "Algorand",
                        amount = 5000.0,
                        purchasePrice = 0.50,
                        purchaseDate = System.currentTimeMillis() - 180 * 86400000L,
                        imageUrl = "https://assets.coingecko.com/coins/images/4380/large/algorand.png"
                    ),
                    currentPrice = 0.364,
                    currentValue = 1820.0,
                    costBasis = 2500.0,
                    profitLoss = -680.0,
                    profitLossPercent = -27.20,
                    profitLoss24h = -72.80,
                    profitLossPercent24h = -3.85
                )
            )
        )
    )
}