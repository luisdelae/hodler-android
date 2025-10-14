package com.luisd.hodler.data.mapper

import com.luisd.hodler.data.remote.dto.CoinDto
import com.luisd.hodler.domain.model.Coin

fun CoinDto.toDomain(): Coin {
    return Coin(
        id = id,
        symbol = symbol,
        name = name,
        image = image,
        currentPrice = currentPrice,
        priceChangePercentage24h = priceChangePercentage24h,
        marketCap = marketCap,
        marketCapRank = marketCapRank,
    )
}