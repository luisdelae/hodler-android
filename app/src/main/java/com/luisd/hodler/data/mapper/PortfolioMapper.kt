package com.luisd.hodler.data.mapper

import com.luisd.hodler.data.local.entity.HoldingEntity
import com.luisd.hodler.domain.model.Holding

fun HoldingEntity.toDomain(): Holding {
    return Holding(
        id = id,
        coinId = coinId,
        coinSymbol = coinSymbol,
        coinName = coinName,
        amount = amount,
        purchasePrice = purchasePrice,
        purchaseDate = purchaseDate,
        imageUrl = imageUrl,
    )
}

fun Holding.toEntity(): HoldingEntity {
    return HoldingEntity(
        id = id,
        coinId = coinId,
        coinSymbol = coinSymbol,
        coinName = coinName,
        amount = amount,
        purchasePrice = purchasePrice,
        purchaseDate = purchaseDate,
        imageUrl = imageUrl,
    )
}