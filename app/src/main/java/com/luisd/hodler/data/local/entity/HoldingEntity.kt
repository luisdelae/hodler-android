package com.luisd.hodler.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "holdings")
data class HoldingEntity(
    @PrimaryKey val id: String,
    val coinId: String,
    val coinSymbol: String,
    val coinName: String,
    val amount: Double,
    val purchasePrice: Double,
    val purchaseDate: Long,
    val imageUrl: String
)