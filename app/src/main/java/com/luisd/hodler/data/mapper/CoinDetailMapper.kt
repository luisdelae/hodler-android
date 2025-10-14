package com.luisd.hodler.data.mapper

import com.luisd.hodler.data.remote.dto.CoinDetailDto
import com.luisd.hodler.domain.model.CoinDetail

private const val USD: String = "usd"

fun CoinDetailDto.toDomain(): CoinDetail {
    return CoinDetail(
        id = id,
        symbol = symbol,
        name = name,
        image = image.large,
        currentPrice = marketData.currentPrice[USD] ?: 0.0,
        marketCapUsd = marketData.marketCap[USD] ?: 0.0,
        marketCapRank = marketData.marketCapRank,
        totalVolumeUsd = marketData.totalVolume[USD] ?: 0.0,
        priceChangePercentage24h = marketData.priceChangePercentage24h,
        circulatingSupply = marketData.circulatingSupply,
        totalSupply = marketData.totalSupply,
        maxSupply = marketData.maxSupply,
        allTimeHighUsd = marketData.allTimeHigh[USD] ?: 0.0,
        allTimeHighUsdDate = marketData.allTimeHighDate[USD] ?: "",
        allTimeLowUsd = marketData.allTimeLow[USD] ?: 0.0,
        allTimeLowUsdDate = marketData.allTimeLowDate[USD] ?: ""
    )
}