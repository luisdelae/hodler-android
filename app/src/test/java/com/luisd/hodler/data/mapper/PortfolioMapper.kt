package com.luisd.hodler.data.mapper

import com.luisd.hodler.data.local.entity.HoldingEntity
import com.luisd.hodler.domain.model.Holding
import org.junit.Assert.assertEquals
import org.junit.Test

class PortfolioMapperTest {

    @Test
    fun `HoldingEntity toDomain maps all fields correctly`() {
        // Arrange
        val entity = HoldingEntity(
            id = 1L,
            coinId = "bitcoin",
            coinSymbol = "BTC",
            coinName = "Bitcoin",
            imageUrl = "https://example.com/bitcoin.png",
            amount = 0.5,
            purchasePrice = 50000.0,
            purchaseDate = 1699920000000L
        )

        // Act
        val domain = entity.toDomain()

        // Assert
        assertEquals(1L, domain.id)
        assertEquals("bitcoin", domain.coinId)
        assertEquals("BTC", domain.coinSymbol)
        assertEquals("Bitcoin", domain.coinName)
        assertEquals("https://example.com/bitcoin.png", domain.imageUrl)
        assertEquals(0.5, domain.amount, 0.00001)
        assertEquals(50000.0, domain.purchasePrice, 0.01)
        assertEquals(1699920000000L, domain.purchaseDate)
    }

    @Test
    fun `Holding toEntity maps all fields correctly`() {
        // Arrange
        val domain = Holding(
            id = 1L,
            coinId = "bitcoin",
            coinSymbol = "BTC",
            coinName = "Bitcoin",
            imageUrl = "https://example.com/bitcoin.png",
            amount = 0.5,
            purchasePrice = 50000.0,
            purchaseDate = 1699920000000L
        )

        // Act
        val entity = domain.toEntity()

        // Assert
        assertEquals(1L, entity.id)
        assertEquals("bitcoin", entity.coinId)
        assertEquals("BTC", entity.coinSymbol)
        assertEquals("Bitcoin", entity.coinName)
        assertEquals("https://example.com/bitcoin.png", entity.imageUrl)
        assertEquals(0.5, entity.amount, 0.00001)
        assertEquals(50000.0, entity.purchasePrice, 0.01)
        assertEquals(1699920000000L, entity.purchaseDate)
    }

    @Test
    fun `Entity to domain to entity round trip preserves all data`() {
        // Arrange
        val originalEntity = HoldingEntity(
            id = 1L,
            coinId = "bitcoin",
            coinSymbol = "BTC",
            coinName = "Bitcoin",
            imageUrl = "https://example.com/bitcoin.png",
            amount = 0.5,
            purchasePrice = 50000.0,
            purchaseDate = 1699920000000L
        )

        // Act
        val domain = originalEntity.toDomain()
        val backToEntity = domain.toEntity()

        // Assert - Should be identical to original
        assertEquals(originalEntity.id, backToEntity.id)
        assertEquals(originalEntity.coinId, backToEntity.coinId)
        assertEquals(originalEntity.coinSymbol, backToEntity.coinSymbol)
        assertEquals(originalEntity.coinName, backToEntity.coinName)
        assertEquals(originalEntity.imageUrl, backToEntity.imageUrl)
        assertEquals(originalEntity.amount, backToEntity.amount, 0.00001)
        assertEquals(originalEntity.purchasePrice, backToEntity.purchasePrice, 0.01)
        assertEquals(originalEntity.purchaseDate, backToEntity.purchaseDate)
    }

    @Test
    fun `Domain to entity to domain round trip preserves all data`() {
        // Arrange
        val originalDomain = Holding(
            id = 1L,
            coinId = "bitcoin",
            coinSymbol = "BTC",
            coinName = "Bitcoin",
            imageUrl = "https://example.com/bitcoin.png",
            amount = 0.5,
            purchasePrice = 50000.0,
            purchaseDate = 1699920000000L
        )

        // Act
        val entity = originalDomain.toEntity()
        val backToDomain = entity.toDomain()

        // Assert - Should be identical to original
        assertEquals(originalDomain.id, backToDomain.id)
        assertEquals(originalDomain.coinId, backToDomain.coinId)
        assertEquals(originalDomain.coinSymbol, backToDomain.coinSymbol)
        assertEquals(originalDomain.coinName, backToDomain.coinName)
        assertEquals(originalDomain.imageUrl, backToDomain.imageUrl)
        assertEquals(originalDomain.amount, backToDomain.amount, 0.00001)
        assertEquals(originalDomain.purchasePrice, backToDomain.purchasePrice, 0.01)
        assertEquals(originalDomain.purchaseDate, backToDomain.purchaseDate)
    }

    @Test
    fun `Holding with special characters in coin name maps correctly`() {
        // Arrange
        val domain = Holding(
            id = 1L,
            coinId = "wrapped-bitcoin",
            coinSymbol = "WBTC",
            coinName = "Wrapped Bitcoin (WBTC)",
            imageUrl = "https://example.com/wbtc.png",
            amount = 0.5,
            purchasePrice = 50000.0,
            purchaseDate = System.currentTimeMillis()
        )

        // Act
        val entity = domain.toEntity()
        val backToDomain = entity.toDomain()

        // Assert
        assertEquals("Wrapped Bitcoin (WBTC)", backToDomain.coinName)
    }
}
