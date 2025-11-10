package com.luisd.hodler.domain.usecase

import app.cash.turbine.test
import com.luisd.hodler.domain.model.Holding
import com.luisd.hodler.domain.model.PriceData
import com.luisd.hodler.domain.model.Result
import com.luisd.hodler.domain.repository.CoinRepository
import com.luisd.hodler.domain.repository.PortfolioRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class ObservePortfolioUseCaseTest {

    private lateinit var useCase: ObservePortfolioUseCase
    private lateinit var mockPortfolioRepository: PortfolioRepository
    private lateinit var mockCoinRepository: CoinRepository

    private val mockBitcoinHolding = Holding(
        id = 1L,
        coinId = "bitcoin",
        coinSymbol = "BTC",
        coinName = "Bitcoin",
        imageUrl = "https://example.com/bitcoin.png",
        amount = 0.5,
        purchasePrice = 50000.0,
        purchaseDate = System.currentTimeMillis() - 86400000
    )

    private val mockEthereumHolding = Holding(
        id = 2L,
        coinId = "ethereum",
        coinSymbol = "ETH",
        coinName = "Ethereum",
        imageUrl = "https://example.com/ethereum.png",
        amount = 2.0,
        purchasePrice = 3000.0,
        purchaseDate = System.currentTimeMillis() - 172800000
    )

    private val mockBitcoinPrice = PriceData(
        usd = 54000.0, // Up from purchase price of 50000
        usd24hChange = 2.0 // Up 2% in 24h
    )

    private val mockEthereumPrice = PriceData(
        usd = 2800.0, // Down from purchase price of 3000
        usd24hChange = -3.0 // Down 3% in 24h
    )

    @Before
    fun setup() {
        mockPortfolioRepository = mockk()
        mockCoinRepository = mockk()
        useCase = ObservePortfolioUseCase(mockPortfolioRepository, mockCoinRepository)
    }

    // ==================== Happy Path Tests ====================

    @Test
    fun `invoke returns holdings with current prices when both repositories succeed`() = runTest {
        // Arrange
        val holdings = listOf(mockBitcoinHolding, mockEthereumHolding)
        val prices = mapOf(
            "bitcoin" to mockBitcoinPrice,
            "ethereum" to mockEthereumPrice
        )

        coEvery { mockPortfolioRepository.getAllHoldings() } returns flowOf(
            Result.Success(holdings, isFromCache = false, lastUpdated = System.currentTimeMillis())
        )
        coEvery { mockCoinRepository.getCurrentPrices(listOf("bitcoin", "ethereum")) } returns
                Result.Success(prices, isFromCache = false, lastUpdated = System.currentTimeMillis())

        // Act & Assert
        useCase().test {
            val result = awaitItem()
            assertTrue(result is Result.Success)

            val success = result as Result.Success
            assertEquals(2, success.data.size)

            // Verify Bitcoin holding with price
            val bitcoinWithPrice = success.data.find { it.holding.coinId == "bitcoin" }!!
            assertEquals(54000.0, bitcoinWithPrice.currentPrice, 0.01)
            assertEquals(27000.0, bitcoinWithPrice.currentValue, 0.01) // 0.5 * 54000
            assertEquals(25000.0, bitcoinWithPrice.costBasis, 0.01) // 0.5 * 50000
            assertEquals(2000.0, bitcoinWithPrice.profitLoss, 0.01) // 27000 - 25000
            assertEquals(8.0, bitcoinWithPrice.profitLossPercent, 0.01) // (2000 / 25000) * 100

            // Verify Ethereum holding with price
            val ethereumWithPrice = success.data.find { it.holding.coinId == "ethereum" }!!
            assertEquals(2800.0, ethereumWithPrice.currentPrice, 0.01)
            assertEquals(5600.0, ethereumWithPrice.currentValue, 0.01) // 2.0 * 2800
            assertEquals(6000.0, ethereumWithPrice.costBasis, 0.01) // 2.0 * 3000
            assertEquals(-400.0, ethereumWithPrice.profitLoss, 0.01) // 5600 - 6000
            assertEquals(-6.67, ethereumWithPrice.profitLossPercent, 0.01) // (-400 / 6000) * 100

            awaitComplete()
        }
    }

    @Test
    fun `invoke preserves cache metadata from price repository`() = runTest {
        // Arrange
        val holdings = listOf(mockBitcoinHolding)
        val prices = mapOf("bitcoin" to mockBitcoinPrice)
        val cacheTimestamp = System.currentTimeMillis() - 300000 // 5 minutes ago

        coEvery { mockPortfolioRepository.getAllHoldings() } returns flowOf(
            Result.Success(holdings, isFromCache = false, lastUpdated = System.currentTimeMillis())
        )
        coEvery { mockCoinRepository.getCurrentPrices(listOf("bitcoin")) } returns
                Result.Success(prices, isFromCache = true, lastUpdated = cacheTimestamp)

        // Act & Assert
        useCase().test {
            val result = awaitItem() as Result.Success
            assertTrue(result.isFromCache)
            assertEquals(cacheTimestamp, result.lastUpdated)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns empty list when user has no holdings`() = runTest {
        // Arrange
        coEvery { mockPortfolioRepository.getAllHoldings() } returns flowOf(
            Result.Success(emptyList(), isFromCache = false, lastUpdated = System.currentTimeMillis())
        )

        // Act & Assert
        useCase().test {
            val result = awaitItem()
            assertTrue(result is Result.Success)

            val success = result as Result.Success
            assertTrue(success.data.isEmpty())
            assertFalse(success.isFromCache)
            awaitComplete()
        }
    }

    // ==================== Price Calculation Tests ====================

    @Test
    fun `invoke calculates 24h profit loss correctly`() = runTest {
        // Arrange - Bitcoin is up 2% in 24h
        val holdings = listOf(mockBitcoinHolding)
        val prices = mapOf("bitcoin" to mockBitcoinPrice)

        coEvery { mockPortfolioRepository.getAllHoldings() } returns flowOf(
            Result.Success(holdings, isFromCache = false, lastUpdated = System.currentTimeMillis())
        )
        coEvery { mockCoinRepository.getCurrentPrices(listOf("bitcoin")) } returns
                Result.Success(prices, isFromCache = false, lastUpdated = System.currentTimeMillis())

        // Act & Assert
        useCase().test {
            val result = awaitItem() as Result.Success
            val bitcoinWithPrice = result.data[0]

            // Current price: 54000
            // Price 24h ago: 54000 / 1.02 ≈ 52941.18
            // 24h change: (54000 - 52941.18) * 0.5 ≈ 529.41
            val expectedPrice24hAgo = 54000.0 / 1.02
            val expected24hProfitLoss = (54000.0 - expectedPrice24hAgo) * 0.5

            assertEquals(expected24hProfitLoss, bitcoinWithPrice.profitLoss24h, 0.01)
            assertEquals(2.0, bitcoinWithPrice.profitLossPercent24h, 0.01)
            awaitComplete()
        }
    }

    @Test
    fun `invoke handles negative 24h price change correctly`() = runTest {
        // Arrange - Ethereum is down 3% in 24h
        val holdings = listOf(mockEthereumHolding)
        val prices = mapOf("ethereum" to mockEthereumPrice)

        coEvery { mockPortfolioRepository.getAllHoldings() } returns flowOf(
            Result.Success(holdings, isFromCache = false, lastUpdated = System.currentTimeMillis())
        )
        coEvery { mockCoinRepository.getCurrentPrices(listOf("ethereum")) } returns
                Result.Success(prices, isFromCache = false, lastUpdated = System.currentTimeMillis())

        // Act & Assert
        useCase().test {
            val result = awaitItem() as Result.Success
            val ethereumWithPrice = result.data[0]

            // Current price: 2800
            // Price 24h ago: 2800 / 0.97 ≈ 2886.60
            // 24h change: (2800 - 2886.60) * 2.0 ≈ -173.20
            val expectedPrice24hAgo = 2800.0 / 0.97
            val expected24hProfitLoss = (2800.0 - expectedPrice24hAgo) * 2.0

            assertEquals(expected24hProfitLoss, ethereumWithPrice.profitLoss24h, 0.01)
            assertEquals(-3.0, ethereumWithPrice.profitLossPercent24h, 0.01)
            awaitComplete()
        }
    }

    @Test
    fun `invoke handles zero 24h price change correctly`() = runTest {
        // Arrange - Price unchanged in 24h
        val holdings = listOf(mockBitcoinHolding)
        val prices = mapOf(
            "bitcoin" to PriceData(usd = 54000.0, usd24hChange = 0.0)
        )

        coEvery { mockPortfolioRepository.getAllHoldings() } returns flowOf(
            Result.Success(holdings, isFromCache = false, lastUpdated = System.currentTimeMillis())
        )
        coEvery { mockCoinRepository.getCurrentPrices(listOf("bitcoin")) } returns
                Result.Success(prices, isFromCache = false, lastUpdated = System.currentTimeMillis())

        // Act & Assert
        useCase().test {
            val result = awaitItem() as Result.Success
            val bitcoinWithPrice = result.data[0]

            assertEquals(0.0, bitcoinWithPrice.profitLoss24h, 0.01)
            assertEquals(0.0, bitcoinWithPrice.profitLossPercent24h, 0.01)
            awaitComplete()
        }
    }

    @Test
    fun `invoke handles missing price data gracefully`() = runTest {
        // Arrange - Price not available for coin
        val holdings = listOf(mockBitcoinHolding)
        val prices = emptyMap<String, PriceData>()

        coEvery { mockPortfolioRepository.getAllHoldings() } returns flowOf(
            Result.Success(holdings, isFromCache = false, lastUpdated = System.currentTimeMillis())
        )
        coEvery { mockCoinRepository.getCurrentPrices(listOf("bitcoin")) } returns
                Result.Success(prices, isFromCache = false, lastUpdated = System.currentTimeMillis())

        // Act & Assert
        useCase().test {
            val result = awaitItem() as Result.Success
            val bitcoinWithPrice = result.data[0]

            // Should default to 0.0 for all price-related fields
            assertEquals(0.0, bitcoinWithPrice.currentPrice, 0.01)
            assertEquals(0.0, bitcoinWithPrice.currentValue, 0.01)
            assertEquals(25000.0, bitcoinWithPrice.costBasis, 0.01) // Purchase value unchanged
            assertEquals(-25000.0, bitcoinWithPrice.profitLoss, 0.01) // All loss since current value is 0
            assertEquals(0.0, bitcoinWithPrice.profitLoss24h, 0.01)
            awaitComplete()
        }
    }

    @Test
    fun `invoke calculates profit loss percentage correctly for zero cost basis`() = runTest {
        // Arrange - Holding with zero purchase price (edge case)
        val freeHolding = mockBitcoinHolding.copy(purchasePrice = 0.0)
        val holdings = listOf(freeHolding)
        val prices = mapOf("bitcoin" to mockBitcoinPrice)

        coEvery { mockPortfolioRepository.getAllHoldings() } returns flowOf(
            Result.Success(holdings, isFromCache = false, lastUpdated = System.currentTimeMillis())
        )
        coEvery { mockCoinRepository.getCurrentPrices(listOf("bitcoin")) } returns
                Result.Success(prices, isFromCache = false, lastUpdated = System.currentTimeMillis())

        // Act & Assert
        useCase().test {
            val result = awaitItem() as Result.Success
            val bitcoinWithPrice = result.data[0]

            assertEquals(0.0, bitcoinWithPrice.costBasis, 0.01)
            assertEquals(27000.0, bitcoinWithPrice.currentValue, 0.01)
            assertEquals(27000.0, bitcoinWithPrice.profitLoss, 0.01)
            assertEquals(0.0, bitcoinWithPrice.profitLossPercent, 0.01) // Avoid division by zero
            awaitComplete()
        }
    }

    // ==================== Flow State Transformation Tests ====================

    @Test
    fun `invoke emits Loading when portfolio repository emits Loading`() = runTest {
        // Arrange
        coEvery { mockPortfolioRepository.getAllHoldings() } returns flowOf(Result.Loading)

        // Act & Assert
        useCase().test {
            val result = awaitItem()
            assertTrue(result is Result.Loading)
            awaitComplete()
        }
    }

    @Test
    fun `invoke emits Error when portfolio repository emits Error`() = runTest {
        // Arrange
        val exception = IOException("Database error")
        coEvery { mockPortfolioRepository.getAllHoldings() } returns flowOf(
            Result.Error(exception)
        )

        // Act & Assert
        useCase().test {
            val result = awaitItem()
            assertTrue(result is Result.Error)

            val error = result as Result.Error
            assertEquals("Database error", error.exception.message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke emits Error when coin repository fails`() = runTest {
        // Arrange
        val holdings = listOf(mockBitcoinHolding)
        val exception = IOException("Network error")

        coEvery { mockPortfolioRepository.getAllHoldings() } returns flowOf(
            Result.Success(holdings, isFromCache = false, lastUpdated = System.currentTimeMillis())
        )
        coEvery { mockCoinRepository.getCurrentPrices(listOf("bitcoin")) } returns
                Result.Error(exception)

        // Act & Assert
        useCase().test {
            val result = awaitItem()
            assertTrue(result is Result.Error)

            val error = result as Result.Error
            assertEquals("Network error", error.exception.message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke emits Loading when coin repository emits Loading`() = runTest {
        // Arrange
        val holdings = listOf(mockBitcoinHolding)

        coEvery { mockPortfolioRepository.getAllHoldings() } returns flowOf(
            Result.Success(holdings, isFromCache = false, lastUpdated = System.currentTimeMillis())
        )
        coEvery { mockCoinRepository.getCurrentPrices(listOf("bitcoin")) } returns
                Result.Loading

        // Act & Assert
        useCase().test {
            val result = awaitItem()
            assertTrue(result is Result.Loading)
            awaitComplete()
        }
    }

    // ==================== Multiple Holdings Tests ====================

    @Test
    fun `invoke correctly aggregates profit loss across multiple holdings`() = runTest {
        // Arrange - Multiple holdings with mixed performance
        val holdings = listOf(mockBitcoinHolding, mockEthereumHolding)
        val prices = mapOf(
            "bitcoin" to mockBitcoinPrice,
            "ethereum" to mockEthereumPrice
        )

        coEvery { mockPortfolioRepository.getAllHoldings() } returns flowOf(
            Result.Success(holdings, isFromCache = false, lastUpdated = System.currentTimeMillis())
        )
        coEvery { mockCoinRepository.getCurrentPrices(listOf("bitcoin", "ethereum")) } returns
                Result.Success(prices, isFromCache = false, lastUpdated = System.currentTimeMillis())

        // Act & Assert
        useCase().test {
            val result = awaitItem() as Result.Success

            // Bitcoin: +2000 profit, Ethereum: -400 loss
            val totalProfitLoss = result.data.sumOf { it.profitLoss }
            assertEquals(1600.0, totalProfitLoss, 0.01) // 2000 - 400

            val totalCurrentValue = result.data.sumOf { it.currentValue }
            assertEquals(32600.0, totalCurrentValue, 0.01) // 27000 + 5600

            val totalCostBasis = result.data.sumOf { it.costBasis }
            assertEquals(31000.0, totalCostBasis, 0.01) // 25000 + 6000

            awaitComplete()
        }
    }

    @Test
    fun `invoke handles duplicate coin IDs by requesting distinct prices`() = runTest {
        // Arrange - Multiple holdings of same coin
        val holding1 = mockBitcoinHolding.copy(id = 1L, amount = 0.5)
        val holding2 = mockBitcoinHolding.copy(id = 2L, amount = 1.0)
        val holdings = listOf(holding1, holding2)
        val prices = mapOf("bitcoin" to mockBitcoinPrice)

        coEvery { mockPortfolioRepository.getAllHoldings() } returns flowOf(
            Result.Success(holdings, isFromCache = false, lastUpdated = System.currentTimeMillis())
        )
        // Should only request "bitcoin" once, not ["bitcoin", "bitcoin"]
        coEvery { mockCoinRepository.getCurrentPrices(listOf("bitcoin")) } returns
                Result.Success(prices, isFromCache = false, lastUpdated = System.currentTimeMillis())

        // Act & Assert
        useCase().test {
            val result = awaitItem() as Result.Success
            assertEquals(2, result.data.size)

            // Both holdings should have correct calculations
            val firstHolding = result.data[0]
            assertEquals(27000.0, firstHolding.currentValue, 0.01) // 0.5 * 54000

            val secondHolding = result.data[1]
            assertEquals(54000.0, secondHolding.currentValue, 0.01) // 1.0 * 54000

            awaitComplete()
        }
    }

    // ==================== Edge Case Tests ====================

    @Test
    fun `invoke handles fractional coin amounts correctly`() = runTest {
        // Arrange
        val fractionalHolding = mockBitcoinHolding.copy(amount = 0.00123456)
        val holdings = listOf(fractionalHolding)
        val prices = mapOf("bitcoin" to mockBitcoinPrice)

        coEvery { mockPortfolioRepository.getAllHoldings() } returns flowOf(
            Result.Success(holdings, isFromCache = false, lastUpdated = System.currentTimeMillis())
        )
        coEvery { mockCoinRepository.getCurrentPrices(listOf("bitcoin")) } returns
                Result.Success(prices, isFromCache = false, lastUpdated = System.currentTimeMillis())

        // Act & Assert
        useCase().test {
            val result = awaitItem() as Result.Success
            val withPrice = result.data[0]

            // 0.00123456 * 54000 = 66.66624
            assertEquals(66.66624, withPrice.currentValue, 0.00001)
            awaitComplete()
        }
    }

    @Test
    fun `invoke handles very large holdings correctly`() = runTest {
        // Arrange
        val largeHolding = mockBitcoinHolding.copy(amount = 1000.0, purchasePrice = 10000.0)
        val holdings = listOf(largeHolding)
        val prices = mapOf("bitcoin" to mockBitcoinPrice)

        coEvery { mockPortfolioRepository.getAllHoldings() } returns flowOf(
            Result.Success(holdings, isFromCache = false, lastUpdated = System.currentTimeMillis())
        )
        coEvery { mockCoinRepository.getCurrentPrices(listOf("bitcoin")) } returns
                Result.Success(prices, isFromCache = false, lastUpdated = System.currentTimeMillis())

        // Act & Assert
        useCase().test {
            val result = awaitItem() as Result.Success
            val withPrice = result.data[0]

            assertEquals(54000000.0, withPrice.currentValue, 0.01) // 1000 * 54000
            assertEquals(10000000.0, withPrice.costBasis, 0.01) // 1000 * 10000
            assertEquals(44000000.0, withPrice.profitLoss, 0.01)
            assertEquals(440.0, withPrice.profitLossPercent, 0.01) // 440% gain
            awaitComplete()
        }
    }

    @Test
    fun `invoke handles extreme price volatility correctly`() = runTest {
        // Arrange - Price up 1000% in 24h
        val holdings = listOf(mockBitcoinHolding)
        val volatilePrice = PriceData(usd = 100000.0, usd24hChange = 1000.0)
        val prices = mapOf("bitcoin" to volatilePrice)

        coEvery { mockPortfolioRepository.getAllHoldings() } returns flowOf(
            Result.Success(holdings, isFromCache = false, lastUpdated = System.currentTimeMillis())
        )
        coEvery { mockCoinRepository.getCurrentPrices(listOf("bitcoin")) } returns
                Result.Success(prices, isFromCache = false, lastUpdated = System.currentTimeMillis())

        // Act & Assert
        useCase().test {
            val result = awaitItem() as Result.Success
            val withPrice = result.data[0]

            assertEquals(100000.0, withPrice.currentPrice, 0.01)
            assertEquals(50000.0, withPrice.currentValue, 0.01) // 0.5 * 100000
            assertEquals(1000.0, withPrice.profitLossPercent24h, 0.01)
            awaitComplete()
        }
    }

    @Test
    fun `invoke preserves original holding data`() = runTest {
        // Arrange
        val holdings = listOf(mockBitcoinHolding)
        val prices = mapOf("bitcoin" to mockBitcoinPrice)

        coEvery { mockPortfolioRepository.getAllHoldings() } returns flowOf(
            Result.Success(holdings, isFromCache = false, lastUpdated = System.currentTimeMillis())
        )
        coEvery { mockCoinRepository.getCurrentPrices(listOf("bitcoin")) } returns
                Result.Success(prices, isFromCache = false, lastUpdated = System.currentTimeMillis())

        // Act & Assert
        useCase().test {
            val result = awaitItem() as Result.Success
            val withPrice = result.data[0]

            // Verify original holding is preserved
            assertEquals(mockBitcoinHolding.id, withPrice.holding.id)
            assertEquals(mockBitcoinHolding.coinId, withPrice.holding.coinId)
            assertEquals(mockBitcoinHolding.coinSymbol, withPrice.holding.coinSymbol)
            assertEquals(mockBitcoinHolding.coinName, withPrice.holding.coinName)
            assertEquals(mockBitcoinHolding.amount, withPrice.holding.amount, 0.00001)
            assertEquals(mockBitcoinHolding.purchasePrice, withPrice.holding.purchasePrice, 0.01)
            assertEquals(mockBitcoinHolding.purchaseDate, withPrice.holding.purchaseDate)
            assertEquals(mockBitcoinHolding.imageUrl, withPrice.holding.imageUrl)
            awaitComplete()
        }
    }
}
