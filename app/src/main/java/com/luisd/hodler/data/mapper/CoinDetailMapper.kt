package com.luisd.hodler.data.mapper

import com.luisd.hodler.data.local.entity.CachedCoinDetailEntity
import com.luisd.hodler.data.remote.dto.CoinDetailDto
import com.luisd.hodler.data.remote.dto.PriceDataDto
import com.luisd.hodler.domain.model.CoinDetail
import com.luisd.hodler.domain.model.PriceData

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

fun PriceDataDto.toDomain(): PriceData {
    return PriceData(
        usd = usd,
        usd24hChange = usd24hChange
    )
}

fun CoinDetailDto.toCachedEntity(): CachedCoinDetailEntity {
    return CachedCoinDetailEntity(
        id = id,
        symbol = symbol,
        name = name,
        image = image.large,

        currentPrice = marketData.currentPrice["usd"] ?: 0.0,
        priceChangePercentage24h = marketData.priceChangePercentage24h,
        marketCapUsd = marketData.marketCap["usd"] ?: 0.0,
        marketCapRank = marketData.marketCapRank,
        totalVolumeUsd = marketData.totalVolume["usd"] ?: 0.0,
        circulatingSupply = marketData.circulatingSupply,
        totalSupply = marketData.totalSupply,
        maxSupply = marketData.maxSupply,
        allTimeHighUsd = marketData.allTimeHigh["usd"] ?: 0.0,
        allTimeHighUsdDate = marketData.allTimeHighDate["usd"] ?: "",
        allTimeLowUsd = marketData.allTimeLow["usd"] ?: 0.0,
        allTimeLowUsdDate = marketData.allTimeLowDate["usd"] ?: "",

        lastUpdated = System.currentTimeMillis()
    )
}

fun CachedCoinDetailEntity.toDomain(): CoinDetail {
    return CoinDetail(
        id = id,
        symbol = symbol,
        name = name,
        image = image,
        currentPrice = currentPrice,
        marketCapUsd = marketCapUsd,
        marketCapRank = marketCapRank,
        totalVolumeUsd = totalVolumeUsd,
        priceChangePercentage24h = priceChangePercentage24h,
        circulatingSupply = circulatingSupply,
        totalSupply = totalSupply,
        maxSupply = maxSupply,
        allTimeHighUsd = allTimeHighUsd,
        allTimeLowUsd = allTimeLowUsd,
        allTimeHighUsdDate = allTimeHighUsdDate,
        allTimeLowUsdDate = allTimeLowUsdDate
    )
}
