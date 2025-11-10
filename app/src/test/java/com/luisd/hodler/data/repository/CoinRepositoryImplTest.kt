package com.luisd.hodler.data.repository

import com.luisd.hodler.data.local.dao.CachedCoinDao
import com.luisd.hodler.data.local.dao.CachedCoinDetailDao
import com.luisd.hodler.data.local.entity.CachedCoinDetailEntity
import com.luisd.hodler.data.local.entity.CachedCoinEntity
import com.luisd.hodler.data.remote.api.CoinGeckoApi
import com.luisd.hodler.data.remote.dto.CoinDetailDto
import com.luisd.hodler.data.remote.dto.CoinDto
import com.luisd.hodler.data.remote.dto.ImageDto
import com.luisd.hodler.data.remote.dto.MarketChartDto
import com.luisd.hodler.data.remote.dto.MarketDataDto
import com.luisd.hodler.data.remote.dto.PriceDataDto
import com.luisd.hodler.domain.model.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class CoinRepositoryImplTest {

    private lateinit var repository: CoinRepositoryImpl
    private lateinit var mockApi: CoinGeckoApi
    private lateinit var mockCachedCoinDao: CachedCoinDao
    private lateinit var mockCachedCoinDetailDao: CachedCoinDetailDao

    private val mockBitcoinDto = CoinDto(
        id = "bitcoin",
        symbol = "btc",
        name = "Bitcoin",
        image = "https://example.com/bitcoin.png",
        currentPrice = 54231.12,
        priceChangePercentage24h = 2.5,
        marketCap = 1000000000L,
        marketCapRank = 1
    )

    private val mockEthereumDto = CoinDto(
        id = "ethereum",
        symbol = "eth",
        name = "Ethereum",
        image = "https://example.com/ethereum.png",
        currentPrice = 4012.58,
        priceChangePercentage24h = -1.5,
        marketCap = 500000000L,
        marketCapRank = 2
    )

    private val mockBitcoinEntity = CachedCoinEntity(
        id = "bitcoin",
        symbol = "btc",
        name = "Bitcoin",
        image = "https://example.com/bitcoin.png",
        currentPrice = 54231.12,
        priceChangePercentage24h = 2.5,
        marketCap = 1000000000L,
        marketCapRank = 1,
        lastUpdated = System.currentTimeMillis() - 60000
    )

    private val mockEthereumEntity = CachedCoinEntity(
        id = "ethereum",
        symbol = "eth",
        name = "Ethereum",
        image = "https://example.com/ethereum.png",
        currentPrice = 4012.58,
        priceChangePercentage24h = -1.5,
        marketCap = 500000000L,
        marketCapRank = 2,
        lastUpdated = System.currentTimeMillis() - 60000
    )

    private val mockBitcoinDetailDto = CoinDetailDto(
        id = "bitcoin",
        symbol = "btc",
        name = "Bitcoin",
        image = ImageDto(large = "https://example.com/bitcoin.png"),
        marketData = MarketDataDto(
            currentPrice = mapOf("usd" to 54231.12),
            priceChangePercentage24h = 2.5,
            marketCap = mapOf("usd" to 1000000000.0),
            marketCapRank = 1,
            totalVolume = mapOf("usd" to 50000000.0),
            circulatingSupply = 19500000.0,
            totalSupply = 21000000.0,
            maxSupply = 21000000.0,
            allTimeHigh = mapOf("usd" to 69000.0),
            allTimeHighDate = mapOf("usd" to "2021-11-10T14:24:11.849Z"),
            allTimeLow = mapOf("usd" to 67.81),
            allTimeLowDate = mapOf("usd" to "2013-07-06T00:00:00.000Z")
        )
    )

    private val mockBitcoinDetailEntity = CachedCoinDetailEntity(
        id = "bitcoin",
        symbol = "btc",
        name = "Bitcoin",
        image = "https://example.com/bitcoin.png",
        currentPrice = 54231.12,
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
        lastUpdated = System.currentTimeMillis() - 60000
    )

    @Before
    fun setup() {
        mockApi = mockk()
        mockCachedCoinDao = mockk(relaxed = true)
        mockCachedCoinDetailDao = mockk(relaxed = true)
        repository = CoinRepositoryImpl(mockApi, mockCachedCoinDao, mockCachedCoinDetailDao)
    }

    // ==================== getMarketCoins Tests ====================

    @Test
    fun `getMarketCoins returns fresh data from API on success`() = runTest {
        // Arrange
        val apiData = listOf(mockBitcoinDto, mockEthereumDto)
        coEvery { mockCachedCoinDao.getAllCachedCoins() } returns flowOf(emptyList())
        coEvery { mockCachedCoinDao.getLastUpdateTime() } returns null
        coEvery { mockApi.getMarketCoins() } returns apiData

        // Act
        val result = repository.getMarketCoins()

        // Assert
        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals(2, successResult.data.size)
        assertEquals("bitcoin", successResult.data[0].id)
        assertEquals("ethereum", successResult.data[1].id)
        assertFalse(successResult.isFromCache)
        coVerify { mockCachedCoinDao.insertAllCoins(any()) }
    }

    @Test
    fun `getMarketCoins returns cached data when API fails and cache exists`() = runTest {
        // Arrange
        val cachedData = listOf(mockBitcoinEntity, mockEthereumEntity)
        val cacheTimestamp = System.currentTimeMillis() - 300000 // 5 min ago
        coEvery { mockCachedCoinDao.getAllCachedCoins() } returns flowOf(cachedData)
        coEvery { mockCachedCoinDao.getLastUpdateTime() } returns cacheTimestamp
        coEvery { mockApi.getMarketCoins() } throws IOException("Network error")

        // Act
        val result = repository.getMarketCoins()

        // Assert
        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals(2, successResult.data.size)
        assertEquals("bitcoin", successResult.data[0].id)
        assertTrue(successResult.isFromCache)
        assertEquals(cacheTimestamp, successResult.lastUpdated)
    }

    @Test
    fun `getMarketCoins returns error when API fails and cache is empty`() = runTest {
        // Arrange
        coEvery { mockCachedCoinDao.getAllCachedCoins() } returns flowOf(emptyList())
        coEvery { mockCachedCoinDao.getLastUpdateTime() } returns null
        coEvery { mockApi.getMarketCoins() } throws IOException("Network error")

        // Act
        val result = repository.getMarketCoins()

        // Assert
        assertTrue(result is Result.Error)
        val errorResult = result as Result.Error
        assertEquals("Network error", errorResult.exception.message)
    }

    @Test
    fun `getMarketCoins updates cache on successful API call`() = runTest {
        // Arrange
        val apiData = listOf(mockBitcoinDto)
        coEvery { mockCachedCoinDao.getAllCachedCoins() } returns flowOf(emptyList())
        coEvery { mockCachedCoinDao.getLastUpdateTime() } returns null
        coEvery { mockApi.getMarketCoins() } returns apiData

        // Act
        repository.getMarketCoins()

        // Assert
        coVerify(exactly = 1) { mockCachedCoinDao.insertAllCoins(any()) }
    }

    // ==================== getCoinDetails Tests ====================

    @Test
    fun `getCoinDetails returns fresh data from API on success`() = runTest {
        // Arrange
        coEvery { mockCachedCoinDetailDao.getCachedCoinDetail("bitcoin") } returns flowOf(null)
        coEvery { mockCachedCoinDetailDao.getLastUpdateTime("bitcoin") } returns null
        coEvery { mockApi.getCoinDetails("bitcoin") } returns mockBitcoinDetailDto

        // Act
        val result = repository.getCoinDetails("bitcoin")

        // Assert
        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals("bitcoin", successResult.data.id)
        assertEquals("Bitcoin", successResult.data.name)
        assertFalse(successResult.isFromCache)
        coVerify { mockCachedCoinDetailDao.insertCoinDetail(any()) }
    }

    @Test
    fun `getCoinDetails returns cached data when API fails and cache exists`() = runTest {
        // Arrange
        val cacheTimestamp = System.currentTimeMillis() - 300000
        coEvery { mockCachedCoinDetailDao.getCachedCoinDetail("bitcoin") } returns flowOf(mockBitcoinDetailEntity)
        coEvery { mockCachedCoinDetailDao.getLastUpdateTime("bitcoin") } returns cacheTimestamp
        coEvery { mockApi.getCoinDetails("bitcoin") } throws IOException("Network error")

        // Act
        val result = repository.getCoinDetails("bitcoin")

        // Assert
        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals("bitcoin", successResult.data.id)
        assertTrue(successResult.isFromCache)
        assertEquals(cacheTimestamp, successResult.lastUpdated)
    }

    @Test
    fun `getCoinDetails returns error when API fails and cache is empty`() = runTest {
        // Arrange
        coEvery { mockCachedCoinDetailDao.getCachedCoinDetail("bitcoin") } returns flowOf(null)
        coEvery { mockCachedCoinDetailDao.getLastUpdateTime("bitcoin") } returns null
        coEvery { mockApi.getCoinDetails("bitcoin") } throws IOException("Network error")

        // Act
        val result = repository.getCoinDetails("bitcoin")

        // Assert
        assertTrue(result is Result.Error)
        val errorResult = result as Result.Error
        assertEquals("Network error", errorResult.exception.message)
    }

    @Test
    fun `getCoinDetails updates cache on successful API call`() = runTest {
        // Arrange
        coEvery { mockCachedCoinDetailDao.getCachedCoinDetail("bitcoin") } returns flowOf(null)
        coEvery { mockCachedCoinDetailDao.getLastUpdateTime("bitcoin") } returns null
        coEvery { mockApi.getCoinDetails("bitcoin") } returns mockBitcoinDetailDto

        // Act
        repository.getCoinDetails("bitcoin")

        // Assert
        coVerify(exactly = 1) { mockCachedCoinDetailDao.insertCoinDetail(any()) }
    }

    // ==================== getCoinById Tests ====================

    @Test
    fun `getCoinById returns fresh data from API on success`() = runTest {
        // Arrange
        coEvery { mockCachedCoinDao.getCachedCoinById("bitcoin") } returns flowOf(null)
        coEvery { mockApi.getMarketCoins(coinIds = "bitcoin", perPage = 1) } returns listOf(mockBitcoinDto)

        // Act
        val result = repository.getCoinById("bitcoin")

        // Assert
        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals("bitcoin", successResult.data.id)
        assertFalse(successResult.isFromCache)
        coVerify { mockCachedCoinDao.insertAllCoins(any()) }
    }

    @Test
    fun `getCoinById returns cached data when API fails and cache exists`() = runTest {
        // Arrange
        coEvery { mockCachedCoinDao.getCachedCoinById("bitcoin") } returns flowOf(mockBitcoinEntity)
        coEvery { mockApi.getMarketCoins(coinIds = "bitcoin", perPage = 1) } throws IOException("Network error")

        // Act
        val result = repository.getCoinById("bitcoin")

        // Assert
        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals("bitcoin", successResult.data.id)
        assertTrue(successResult.isFromCache)
        assertEquals(mockBitcoinEntity.lastUpdated, successResult.lastUpdated)
    }

    @Test
    fun `getCoinById returns error when API fails and cache is empty`() = runTest {
        // Arrange
        coEvery { mockCachedCoinDao.getCachedCoinById("bitcoin") } returns flowOf(null)
        coEvery { mockApi.getMarketCoins(coinIds = "bitcoin", perPage = 1) } throws IOException("Network error")

        // Act
        val result = repository.getCoinById("bitcoin")

        // Assert
        assertTrue(result is Result.Error)
    }

    @Test
    fun `getCoinById returns error when API returns empty list and no cache`() = runTest {
        // Arrange
        coEvery { mockCachedCoinDao.getCachedCoinById("unknown") } returns flowOf(null)
        coEvery { mockApi.getMarketCoins(coinIds = "unknown", perPage = 1) } returns emptyList()

        // Act
        val result = repository.getCoinById("unknown")

        // Assert
        assertTrue(result is Result.Error)
        val errorResult = result as Result.Error
        assertEquals("Coin not found", errorResult.exception.message)
    }

    @Test
    fun `getCoinById returns cached data when API returns empty list but cache exists`() = runTest {
        // Arrange
        coEvery { mockCachedCoinDao.getCachedCoinById("bitcoin") } returns flowOf(mockBitcoinEntity)
        coEvery { mockApi.getMarketCoins(coinIds = "bitcoin", perPage = 1) } returns emptyList()

        // Act
        val result = repository.getCoinById("bitcoin")

        // Assert
        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals("bitcoin", successResult.data.id)
        assertTrue(successResult.isFromCache)
    }

    // ==================== getMarketChart Tests ====================

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `getMarketChart returns data from API on success`() = runTest {
        // Arrange
        val mockChartDto = MarketChartDto(
            prices = listOf(
                listOf(1699920000000L, 54000.0),
                listOf(1699923600000L, 54500.0)
            ) as List<List<Double>>
        )
        coEvery { mockApi.getCoinMarketChart(coinId = "bitcoin", days = "7") } returns mockChartDto

        // Act
        val result = repository.getMarketChart("bitcoin", 7)

        // Assert
        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals(2, successResult.data.prices.size)
        assertFalse(successResult.isFromCache)
    }

    @Test
    fun `getMarketChart returns error when API fails`() = runTest {
        // Arrange
        coEvery { mockApi.getCoinMarketChart(coinId = "bitcoin", days = "7") } throws IOException("Network error")

        // Act
        val result = repository.getMarketChart("bitcoin", 7)

        // Assert
        assertTrue(result is Result.Error)
        val errorResult = result as Result.Error
        assertEquals("Network error", errorResult.exception.message)
    }

    @Test
    fun `getMarketChart with different days parameter calls API correctly`() = runTest {
        // Arrange
        val mockChartDto = MarketChartDto(prices = emptyList())
        coEvery { mockApi.getCoinMarketChart(coinId = "ethereum", days = "30") } returns mockChartDto

        // Act
        repository.getMarketChart("ethereum", 30)

        // Assert
        coVerify { mockApi.getCoinMarketChart(coinId = "ethereum", days = "30") }
    }

    // ==================== getCurrentPrices Tests ====================

    @Test
    fun `getCurrentPrices returns fresh data from API on success`() = runTest {
        // Arrange
        val mockPriceResponse = mapOf(
            "bitcoin" to PriceDataDto(
                usd = 54231.12,
                usd24hChange = 2.5
            ),
            "ethereum" to PriceDataDto(
                usd = 4012.58,
                usd24hChange = -1.5
            )
        )
        coEvery { mockApi.getCurrentPrices(coinIds = "bitcoin,ethereum") } returns mockPriceResponse

        // Act
        val result = repository.getCurrentPrices(listOf("bitcoin", "ethereum"))

        // Assert
        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals(2, successResult.data.size)
        successResult.data["bitcoin"]?.let { assertEquals(54231.12, it.usd, 0.01) }
        successResult.data["ethereum"]?.let { assertEquals(4012.58, it.usd, 0.01) }
        assertFalse(successResult.isFromCache)
    }

    @Test
    fun `getCurrentPrices returns cached data when API fails and cache exists`() = runTest {
        // Arrange
        val cachedData = listOf(mockBitcoinEntity, mockEthereumEntity)
        val cacheTimestamp = System.currentTimeMillis() - 300000
        coEvery { mockCachedCoinDao.getAllCachedCoins() } returns flowOf(cachedData)
        coEvery { mockCachedCoinDao.getLastUpdateTime() } returns cacheTimestamp
        coEvery { mockApi.getCurrentPrices(coinIds = "bitcoin,ethereum") } throws IOException("Network error")

        // Act
        val result = repository.getCurrentPrices(listOf("bitcoin", "ethereum"))

        // Assert
        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals(2, successResult.data.size)
        successResult.data["bitcoin"]?.usd?.let { assertEquals(54231.12, it, 0.01) }
        successResult.data["ethereum"]?.usd?.let { assertEquals(4012.58, it, 0.01) }
        assertTrue(successResult.isFromCache)
        assertEquals(cacheTimestamp, successResult.lastUpdated)
    }

    @Test
    fun `getCurrentPrices returns error when API fails and no cached data for requested coins`() = runTest {
        // Arrange
        coEvery { mockCachedCoinDao.getAllCachedCoins() } returns flowOf(emptyList())
        coEvery { mockCachedCoinDao.getLastUpdateTime() } returns null
        coEvery { mockApi.getCurrentPrices(coinIds = "bitcoin") } throws IOException("Network error")

        // Act
        val result = repository.getCurrentPrices(listOf("bitcoin"))

        // Assert
        assertTrue(result is Result.Error)
        val errorResult = result as Result.Error
        assertEquals("Network error", errorResult.exception.message)
    }

    @Test
    fun `getCurrentPrices returns partial cached data when API fails`() = runTest {
        // Arrange
        val cachedData = listOf(mockBitcoinEntity) // Only bitcoin cached
        val cacheTimestamp = System.currentTimeMillis() - 300000
        coEvery { mockCachedCoinDao.getAllCachedCoins() } returns flowOf(cachedData)
        coEvery { mockCachedCoinDao.getLastUpdateTime() } returns cacheTimestamp
        coEvery { mockApi.getCurrentPrices(coinIds = "bitcoin,ethereum,cardano") } throws IOException("Network error")

        // Act
        val result = repository.getCurrentPrices(listOf("bitcoin", "ethereum", "cardano"))

        // Assert
        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals(1, successResult.data.size) // Only bitcoin available
        assertTrue(successResult.data.containsKey("bitcoin"))
        assertFalse(successResult.data.containsKey("ethereum"))
        assertTrue(successResult.isFromCache)
    }

    @Test
    fun `getCurrentPrices formats coin IDs as comma-separated string`() = runTest {
        // Arrange
        val mockPriceResponse = mapOf(
            "bitcoin" to PriceDataDto(usd = 54231.12, usd24hChange = 2.5)
        )
        coEvery { mockApi.getCurrentPrices(coinIds = any()) } returns mockPriceResponse

        // Act
        repository.getCurrentPrices(listOf("bitcoin", "ethereum", "cardano"))

        // Assert
        coVerify { mockApi.getCurrentPrices(coinIds = "bitcoin,ethereum,cardano") }
    }

    @Test
    fun `getCurrentPrices handles single coin ID`() = runTest {
        // Arrange
        val mockPriceResponse = mapOf(
            "bitcoin" to PriceDataDto(usd = 54231.12, usd24hChange = 2.5)
        )
        coEvery { mockApi.getCurrentPrices(coinIds = "bitcoin") } returns mockPriceResponse

        // Act
        val result = repository.getCurrentPrices(listOf("bitcoin"))

        // Assert
        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals(1, successResult.data.size)
        coVerify { mockApi.getCurrentPrices(coinIds = "bitcoin") }
    }

    @Test
    fun `getCurrentPrices handles empty coin list gracefully`() = runTest {
        // Arrange
        coEvery { mockApi.getCurrentPrices(coinIds = "") } returns emptyMap()

        // Act
        val result = repository.getCurrentPrices(emptyList())

        // Assert
        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertTrue(successResult.data.isEmpty())
    }
}
