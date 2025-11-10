package com.luisd.hodler.data.mapper

import com.luisd.hodler.data.local.entity.CachedCoinEntity
import com.luisd.hodler.data.remote.dto.CoinDto
import org.junit.Assert.assertEquals
import org.junit.Test

class CoinMapperTest {

    @Test
    fun `CoinDto toDomain maps all fields correctly`() {
        // Arrange
        val dto = CoinDto(
            id = "bitcoin",
            symbol = "btc",
            name = "Bitcoin",
            image = "https://example.com/bitcoin.png",
            currentPrice = 54231.12,
            priceChangePercentage24h = 2.5,
            marketCap = 1000000000L,
            marketCapRank = 1
        )

        // Act
        val domain = dto.toDomain()

        // Assert
        assertEquals("bitcoin", domain.id)
        assertEquals("btc", domain.symbol)
        assertEquals("Bitcoin", domain.name)
        assertEquals("https://example.com/bitcoin.png", domain.image)
        assertEquals(54231.12, domain.currentPrice, 0.01)
        assertEquals(2.5, domain.priceChangePercentage24h, 0.01)
        assertEquals(1000000000L, domain.marketCap)
        assertEquals(1, domain.marketCapRank)
    }

    @Test
    fun `CachedCoinEntity toDomain maps all fields correctly`() {
        // Arrange
        val entity = CachedCoinEntity(
            id = "bitcoin",
            symbol = "btc",
            name = "Bitcoin",
            image = "https://example.com/bitcoin.png",
            currentPrice = 54231.12,
            priceChangePercentage24h = 2.5,
            marketCap = 1000000000L,
            marketCapRank = 1,
            lastUpdated = System.currentTimeMillis()
        )

        // Act
        val domain = entity.toDomain()

        // Assert
        assertEquals("bitcoin", domain.id)
        assertEquals("btc", domain.symbol)
        assertEquals("Bitcoin", domain.name)
        assertEquals("https://example.com/bitcoin.png", domain.image)
        assertEquals(54231.12, domain.currentPrice, 0.01)
        assertEquals(2.5, domain.priceChangePercentage24h, 0.01)
        assertEquals(1000000000L, domain.marketCap)
        assertEquals(1, domain.marketCapRank)
    }

    @Test
    fun `CoinDto toCachedEntity maps all fields correctly`() {
        // Arrange
        val dto = CoinDto(
            id = "bitcoin",
            symbol = "btc",
            name = "Bitcoin",
            image = "https://example.com/bitcoin.png",
            currentPrice = 54231.12,
            priceChangePercentage24h = 2.5,
            marketCap = 1000000000L,
            marketCapRank = 1
        )

        // Act
        val entity = dto.toCachedEntity()

        // Assert
        assertEquals("bitcoin", entity.id)
        assertEquals("btc", entity.symbol)
        assertEquals("Bitcoin", entity.name)
        assertEquals("https://example.com/bitcoin.png", entity.image)
        assertEquals(54231.12, entity.currentPrice, 0.01)
        assertEquals(2.5, entity.priceChangePercentage24h, 0.01)
        assertEquals(1000000000L, entity.marketCap)
        assertEquals(1, entity.marketCapRank)
    }

    @Test
    fun `CoinDto toCachedEntity sets lastUpdated timestamp`() {
        // Arrange
        val dto = CoinDto(
            id = "bitcoin",
            symbol = "btc",
            name = "Bitcoin",
            image = "https://example.com/bitcoin.png",
            currentPrice = 54231.12,
            priceChangePercentage24h = 2.5,
            marketCap = 1000000000L,
            marketCapRank = 1
        )

        val beforeTime = System.currentTimeMillis()

        // Act
        val entity = dto.toCachedEntity()

        val afterTime = System.currentTimeMillis()

        // Assert
        assert(entity.lastUpdated >= beforeTime)
        assert(entity.lastUpdated <= afterTime)
    }

    @Test
    fun `CoinDto to domain to entity round trip preserves data`() {
        // Arrange
        val originalDto = CoinDto(
            id = "bitcoin",
            symbol = "btc",
            name = "Bitcoin",
            image = "https://example.com/bitcoin.png",
            currentPrice = 54231.12,
            priceChangePercentage24h = 2.5,
            marketCap = 1000000000L,
            marketCapRank = 1
        )

        // Act
        val domain = originalDto.toDomain()
        val entity = originalDto.toCachedEntity()
        val domainFromEntity = entity.toDomain()

        // Assert - Domain objects should be identical
        assertEquals(domain.id, domainFromEntity.id)
        assertEquals(domain.symbol, domainFromEntity.symbol)
        assertEquals(domain.name, domainFromEntity.name)
        assertEquals(domain.image, domainFromEntity.image)
        assertEquals(domain.currentPrice, domainFromEntity.currentPrice, 0.01)
        assertEquals(
            domain.priceChangePercentage24h,
            domainFromEntity.priceChangePercentage24h,
            0.01
        )
        assertEquals(domain.marketCap, domainFromEntity.marketCap)
        assertEquals(domain.marketCapRank, domainFromEntity.marketCapRank)
    }
}
