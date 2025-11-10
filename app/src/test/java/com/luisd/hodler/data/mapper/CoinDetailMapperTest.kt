package com.luisd.hodler.data.mapper

import com.luisd.hodler.data.local.entity.CachedCoinDetailEntity
import com.luisd.hodler.data.remote.dto.CoinDetailDto
import com.luisd.hodler.data.remote.dto.ImageDto
import com.luisd.hodler.data.remote.dto.MarketDataDto
import com.luisd.hodler.data.remote.dto.PriceDataDto
import org.junit.Assert.assertEquals
import org.junit.Test

class CoinDetailMapperTest {

    private fun createMockMarketDataDto(
        currentPrice: Double = 54000.0,
        priceChangePercentage24h: Double = 2.5,
        marketCap: Double = 1000000000.0,
        marketCapRank: Int = 1,
        totalVolume: Double = 50000000.0,
        circulatingSupply: Double = 19500000.0,
        totalSupply: Double? = 21000000.0,
        maxSupply: Double? = 21000000.0,
        allTimeHigh: Double = 69000.0,
        allTimeHighDate: String = "2021-11-10T14:24:11.849Z",
        allTimeLow: Double = 67.81,
        allTimeLowDate: String = "2013-07-06T00:00:00.000Z"
    ) = MarketDataDto(
        currentPrice = mapOf("usd" to currentPrice),
        priceChangePercentage24h = priceChangePercentage24h,
        marketCap = mapOf("usd" to marketCap),
        marketCapRank = marketCapRank,
        totalVolume = mapOf("usd" to totalVolume),
        circulatingSupply = circulatingSupply,
        totalSupply = totalSupply,
        maxSupply = maxSupply,
        allTimeHigh = mapOf("usd" to allTimeHigh),
        allTimeHighDate = mapOf("usd" to allTimeHighDate),
        allTimeLow = mapOf("usd" to allTimeLow),
        allTimeLowDate = mapOf("usd" to allTimeLowDate)
    )

    @Test
    fun `CoinDetailDto toDomain maps all fields correctly`() {
        // Arrange
        val dto = CoinDetailDto(
            id = "bitcoin",
            symbol = "btc",
            name = "Bitcoin",
            image = ImageDto(large = "https://example.com/bitcoin.png"),
            marketData = createMockMarketDataDto()
        )

        // Act
        val domain = dto.toDomain()

        // Assert
        assertEquals("bitcoin", domain.id)
        assertEquals("btc", domain.symbol)
        assertEquals("Bitcoin", domain.name)
        assertEquals("https://example.com/bitcoin.png", domain.image)
        assertEquals(54000.0, domain.currentPrice, 0.01)
        assertEquals(1000000000.0, domain.marketCapUsd, 0.01)
        assertEquals(1, domain.marketCapRank)
        assertEquals(50000000.0, domain.totalVolumeUsd, 0.01)
        assertEquals(2.5, domain.priceChangePercentage24h, 0.01)
        assertEquals(19500000.0, domain.circulatingSupply, 0.01)
        assertEquals(21000000.0, domain.totalSupply)
        assertEquals(21000000.0, domain.maxSupply)
        assertEquals(69000.0, domain.allTimeHighUsd, 0.01)
        assertEquals("2021-11-10T14:24:11.849Z", domain.allTimeHighUsdDate)
        assertEquals(67.81, domain.allTimeLowUsd, 0.01)
        assertEquals("2013-07-06T00:00:00.000Z", domain.allTimeLowUsdDate)
    }

    @Test
    fun `CoinDetailDto toDomain handles missing USD in price map`() {
        // Arrange
        val marketData = createMockMarketDataDto().copy(
            currentPrice = mapOf("eur" to 50000.0) // Only EUR, no USD
        )
        val dto = CoinDetailDto(
            id = "bitcoin",
            symbol = "btc",
            name = "Bitcoin",
            image = ImageDto(large = "https://example.com/bitcoin.png"),
            marketData = marketData
        )

        // Act
        val domain = dto.toDomain()

        // Assert - Should default to 0.0
        assertEquals(0.0, domain.currentPrice, 0.01)
    }

    @Test
    fun `PriceDataDto toDomain maps all fields correctly`() {
        // Arrange
        val dto = PriceDataDto(
            usd = 54000.0,
            usd24hChange = 2.5
        )

        // Act
        val domain = dto.toDomain()

        // Assert
        assertEquals(54000.0, domain.usd, 0.01)
        assertEquals(2.5, domain.usd24hChange, 0.01)
    }

    @Test
    fun `CoinDetailDto toCachedEntity maps all fields correctly`() {
        // Arrange
        val dto = CoinDetailDto(
            id = "bitcoin",
            symbol = "btc",
            name = "Bitcoin",
            image = ImageDto(large = "https://example.com/bitcoin.png"),
            marketData = createMockMarketDataDto()
        )

        // Act
        val entity = dto.toCachedEntity()

        // Assert
        assertEquals("bitcoin", entity.id)
        assertEquals("btc", entity.symbol)
        assertEquals("Bitcoin", entity.name)
        assertEquals("https://example.com/bitcoin.png", entity.image)
        assertEquals(54000.0, entity.currentPrice, 0.01)
        assertEquals(2.5, entity.priceChangePercentage24h, 0.01)
        assertEquals(1000000000.0, entity.marketCapUsd, 0.01)
        assertEquals(1, entity.marketCapRank)
        assertEquals(50000000.0, entity.totalVolumeUsd, 0.01)
        assertEquals(19500000.0, entity.circulatingSupply, 0.01)
        assertEquals(21000000.0, entity.totalSupply)
        assertEquals(21000000.0, entity.maxSupply)
        assertEquals(69000.0, entity.allTimeHighUsd, 0.01)
        assertEquals("2021-11-10T14:24:11.849Z", entity.allTimeHighUsdDate)
        assertEquals(67.81, entity.allTimeLowUsd, 0.01)
        assertEquals("2013-07-06T00:00:00.000Z", entity.allTimeLowUsdDate)
    }

    @Test
    fun `CoinDetailDto toCachedEntity sets lastUpdated timestamp`() {
        // Arrange
        val dto = CoinDetailDto(
            id = "bitcoin",
            symbol = "btc",
            name = "Bitcoin",
            image = ImageDto(large = "https://example.com/bitcoin.png"),
            marketData = createMockMarketDataDto()
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
    fun `CoinDetailDto toCachedEntity handles missing USD fields`() {
        // Arrange
        val marketData = MarketDataDto(
            currentPrice = emptyMap(),
            priceChangePercentage24h = 2.5,
            marketCap = emptyMap(),
            marketCapRank = 1,
            totalVolume = emptyMap(),
            circulatingSupply = 19500000.0,
            totalSupply = 21000000.0,
            maxSupply = 21000000.0,
            allTimeHigh = emptyMap(),
            allTimeHighDate = emptyMap(),
            allTimeLow = emptyMap(),
            allTimeLowDate = emptyMap()
        )
        val dto = CoinDetailDto(
            id = "bitcoin",
            symbol = "btc",
            name = "Bitcoin",
            image = ImageDto(large = "https://example.com/bitcoin.png"),
            marketData = marketData
        )

        // Act
        val entity = dto.toCachedEntity()

        // Assert - Should default to 0.0 or empty string
        assertEquals(0.0, entity.currentPrice, 0.01)
        assertEquals(0.0, entity.marketCapUsd, 0.01)
        assertEquals(0.0, entity.totalVolumeUsd, 0.01)
        assertEquals(0.0, entity.allTimeHighUsd, 0.01)
        assertEquals("", entity.allTimeHighUsdDate)
        assertEquals(0.0, entity.allTimeLowUsd, 0.01)
        assertEquals("", entity.allTimeLowUsdDate)
    }

    @Test
    fun `CachedCoinDetailEntity toDomain maps all fields correctly`() {
        // Arrange
        val entity = CachedCoinDetailEntity(
            id = "bitcoin",
            symbol = "btc",
            name = "Bitcoin",
            image = "https://example.com/bitcoin.png",
            currentPrice = 54000.0,
            priceChangePercentage24h = 2.5,
            marketCapUsd = 1000000000.0,
            marketCapRank = 1,
            totalVolumeUsd = 50000000.0,
            circulatingSupply = 19500000.0,
            totalSupply = 21000000.0,
            maxSupply = 21000000.0,
            allTimeHighUsd = 69000.0,
            allTimeHighUsdDate = "2021-11-10T14:24:11.849Z",
            allTimeLowUsd = 67.81,
            allTimeLowUsdDate = "2013-07-06T00:00:00.000Z",
            lastUpdated = System.currentTimeMillis()
        )

        // Act
        val domain = entity.toDomain()

        // Assert
        assertEquals("bitcoin", domain.id)
        assertEquals("btc", domain.symbol)
        assertEquals("Bitcoin", domain.name)
        assertEquals("https://example.com/bitcoin.png", domain.image)
        assertEquals(54000.0, domain.currentPrice, 0.01)
        assertEquals(1000000000.0, domain.marketCapUsd, 0.01)
        assertEquals(1, domain.marketCapRank)
        assertEquals(50000000.0, domain.totalVolumeUsd, 0.01)
        assertEquals(2.5, domain.priceChangePercentage24h, 0.01)
        assertEquals(19500000.0, domain.circulatingSupply, 0.01)
        assertEquals(21000000.0, domain.totalSupply)
        assertEquals(21000000.0, domain.maxSupply)
        assertEquals(69000.0, domain.allTimeHighUsd, 0.01)
        assertEquals("2021-11-10T14:24:11.849Z", domain.allTimeHighUsdDate)
        assertEquals(67.81, domain.allTimeLowUsd, 0.01)
        assertEquals("2013-07-06T00:00:00.000Z", domain.allTimeLowUsdDate)
    }

    @Test
    fun `CoinDetailDto to domain to entity round trip preserves data`() {
        // Arrange
        val originalDto = CoinDetailDto(
            id = "bitcoin",
            symbol = "btc",
            name = "Bitcoin",
            image = ImageDto(large = "https://example.com/bitcoin.png"),
            marketData = createMockMarketDataDto()
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
        assertEquals(domain.marketCapUsd, domainFromEntity.marketCapUsd, 0.01)
        assertEquals(
            domain.priceChangePercentage24h,
            domainFromEntity.priceChangePercentage24h,
            0.01
        )
    }
}
