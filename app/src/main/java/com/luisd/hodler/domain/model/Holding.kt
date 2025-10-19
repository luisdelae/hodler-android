package com.luisd.hodler.domain.model

data class Holding(
    val id: Long,
    val coinId: String,
    val coinSymbol: String,
    val coinName: String,
    val amount: Double,
    val purchasePrice: Double,
    val purchaseDate: Long,
    val imageUrl: String,
)
