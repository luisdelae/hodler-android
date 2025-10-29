package com.luisd.hodler.presentation.ui.portfolio

import app.cash.turbine.test
import com.luisd.hodler.domain.model.Holding
import com.luisd.hodler.domain.model.HoldingWithPrice
import com.luisd.hodler.domain.model.Result
import com.luisd.hodler.domain.repository.PortfolioRepository
import com.luisd.hodler.domain.usecase.ObservePortfolioUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PortfolioViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockObservePortfolioUseCase: ObservePortfolioUseCase
    private lateinit var mockPortfolioRepository: PortfolioRepository

    private val mockBitcoinHolding = Holding(
        id = 1L,
        coinId = "bitcoin",
        coinSymbol = "BTC",
        coinName = "Bitcoin",
        amount = 0.5,
        purchasePrice = 45000.0,
        purchaseDate = System.currentTimeMillis(),
        imageUrl = "https://example.com/btc.png"
    )

    private val mockEthereumHolding = Holding(
        id = 2L,
        coinId = "ethereum",
        coinSymbol = "ETH",
        coinName = "Ethereum",
        amount = 2.0,
        purchasePrice = 3000.0,
        purchaseDate = System.currentTimeMillis(),
        imageUrl = "https://example.com/eth.png"
    )

    private val mockBitcoinWithPrice = HoldingWithPrice(
        holding = mockBitcoinHolding,
        currentPrice = 50000.0,
        currentValue = 25000.0,
        costBasis = 22500.0,
        profitLoss = 2500.0,
        profitLossPercent = 11.11,
        profitLoss24h = 625.0,
        profitLossPercent24h = 2.5
    )

    private val mockEthereumWithPrice = HoldingWithPrice(
        holding = mockEthereumHolding,
        currentPrice = 3500.0,
        currentValue = 7000.0,
        costBasis = 6000.0,
        profitLoss = 1000.0,
        profitLossPercent = 16.67,
        profitLoss24h = 200.0,
        profitLossPercent24h = 2.86
    )

    private val mockHoldings = listOf(mockBitcoinWithPrice, mockEthereumWithPrice)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockObservePortfolioUseCase = mockk()
        mockPortfolioRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        // Arrange
        coEvery { mockObservePortfolioUseCase() } returns flowOf(Result.Loading)

        // Act
        val viewModel = PortfolioViewModel(mockObservePortfolioUseCase, mockPortfolioRepository)

        // Assert
        Assert.assertTrue(viewModel.uiState.value is PortfolioUiState.Loading)
    }

    @Test
    fun `empty holdings returns empty state`() = runTest {
        // Arrange
        coEvery { mockObservePortfolioUseCase() } returns flowOf(Result.Success(emptyList()))

        // Act
        val viewModel = PortfolioViewModel(mockObservePortfolioUseCase, mockPortfolioRepository)

        // Assert
        viewModel.uiState.test {
            Assert.assertTrue(awaitItem() is PortfolioUiState.Loading)
            Assert.assertTrue(awaitItem() is PortfolioUiState.Empty)
        }
    }

    @Test
    fun `successful holdings returns success state with summary`() = runTest {
        // Arrange
        coEvery { mockObservePortfolioUseCase() } returns flowOf(Result.Success(mockHoldings))

        // Act
        val viewModel = PortfolioViewModel(mockObservePortfolioUseCase, mockPortfolioRepository)

        // Assert
        viewModel.uiState.test {
            Assert.assertTrue(awaitItem() is PortfolioUiState.Loading)
            val success = awaitItem() as PortfolioUiState.Success

            // Summary calculations
            Assert.assertEquals(2, success.summary.coinsOwned)
            Assert.assertEquals(32000.0, success.summary.totalValue, 0.01)
            Assert.assertEquals(28500.0, success.summary.totalCostBasis, 0.01)
            Assert.assertEquals(3500.0, success.summary.totalProfitLoss, 0.01)

            // Holdings
            Assert.assertEquals(2, success.holdings.size)
        }
    }

    @Test
    fun `error in use case returns error state`() = runTest {
        // Arrange
        val exception = Exception("Network error")
        coEvery { mockObservePortfolioUseCase() } returns flowOf(Result.Error(exception))

        // Act
        val viewModel = PortfolioViewModel(mockObservePortfolioUseCase, mockPortfolioRepository)

        // Assert
        viewModel.uiState.test {
            Assert.assertTrue(awaitItem() is PortfolioUiState.Loading)
            val error = awaitItem() as PortfolioUiState.Error
            Assert.assertEquals("Network error", error.message)
        }
    }

    @Test
    fun `holdings are grouped by coin id`() = runTest {
        // Arrange
        val btcHolding1 = mockBitcoinWithPrice
        val btcHolding2 = mockBitcoinWithPrice.copy(
            holding = mockBitcoinHolding.copy(
                id = 3L,
                amount = 0.3,
                purchasePrice = 48000.0
            ),
            currentValue = 15000.0,
            costBasis = 14400.0,
            profitLoss = 600.0
        )

        coEvery { mockObservePortfolioUseCase() } returns
                flowOf(Result.Success(listOf(btcHolding1, btcHolding2)))

        // Act
        val viewModel = PortfolioViewModel(mockObservePortfolioUseCase, mockPortfolioRepository)

        // Assert
        viewModel.uiState.test {
            skipItems(1)
            val state = awaitItem() as PortfolioUiState.Success

            Assert.assertEquals(1, state.holdings.size)
            val btcGroup = state.holdings[0]
            Assert.assertEquals("bitcoin", btcGroup.coinId)
            Assert.assertEquals(2, btcGroup.holdingCount)
            Assert.assertEquals(0.8, btcGroup.totalAmount, 0.01)
        }
    }

    @Test
    fun `coin group calculates average cost basis correctly`() = runTest {
        // Arrange
        val btcHolding1 = mockBitcoinWithPrice
        val btcHolding2 = mockBitcoinWithPrice.copy(
            holding = mockBitcoinHolding.copy(
                id = 3L,
                amount = 0.5,
                purchasePrice = 55000.0
            ),
            costBasis = 27500.0
        )

        coEvery { mockObservePortfolioUseCase() } returns
                flowOf(Result.Success(listOf(btcHolding1, btcHolding2)))

        // Act
        val viewModel = PortfolioViewModel(mockObservePortfolioUseCase, mockPortfolioRepository)

        // Assert
        viewModel.uiState.test {
            skipItems(1)
            val state = awaitItem() as PortfolioUiState.Success
            val btcGroup = state.holdings[0]

            Assert.assertEquals(50000.0, btcGroup.averageCostBasis, 0.01)
        }
    }

    @Test
    fun `refresh triggers new data fetch`() = runTest {
        // Arrange
        val initialHoldings = listOf(mockBitcoinWithPrice)
        val refreshedHoldings = listOf(mockBitcoinWithPrice, mockEthereumWithPrice)

        coEvery { mockObservePortfolioUseCase() } returns
                flowOf(Result.Success(initialHoldings)) andThen
                flowOf(Result.Success(refreshedHoldings))

        val viewModel = PortfolioViewModel(mockObservePortfolioUseCase, mockPortfolioRepository)

        // Assert
        viewModel.uiState.test {
            skipItems(1)
            awaitItem()

            // Act
            viewModel.refreshPrices()

            val refreshedState = awaitItem() as PortfolioUiState.Success
            Assert.assertEquals(2, refreshedState.holdings.size)
        }
    }

    @Test
    fun `deleteHolding calls repository`() = runTest {
        // Arrange
        coEvery { mockObservePortfolioUseCase() } returns flowOf(Result.Success(mockHoldings))
        coEvery { mockPortfolioRepository.deleteHoldingById(1L) } returns Result.Success(1)

        val viewModel = PortfolioViewModel(mockObservePortfolioUseCase, mockPortfolioRepository)
        advanceUntilIdle()

        // Act
        viewModel.deleteHolding(1L)
        advanceUntilIdle()

        // Assert
        coVerify { mockPortfolioRepository.deleteHoldingById(1L) }
    }

    @Test
    fun `portfolio summary calculates total profit loss percent correctly`() = runTest {
        // Arrange
        coEvery { mockObservePortfolioUseCase() } returns flowOf(Result.Success(mockHoldings))

        // Act
        val viewModel = PortfolioViewModel(mockObservePortfolioUseCase, mockPortfolioRepository)

        // Assert
        viewModel.uiState.test {
            skipItems(1)
            val state = awaitItem() as PortfolioUiState.Success

            Assert.assertEquals(12.28, state.summary.totalProfitLossPercent, 0.1)
        }
    }

    @Test
    fun `holdings in coin group are sorted by purchase date descending`() = runTest {
        // Arrange
        val oldPurchase = mockBitcoinWithPrice.copy(
            holding = mockBitcoinHolding.copy(
                id = 1L,
                purchaseDate = 1000000000000L
            )
        )
        val newPurchase = mockBitcoinWithPrice.copy(
            holding = mockBitcoinHolding.copy(
                id = 2L,
                purchaseDate = 2000000000000L
            )
        )

        coEvery { mockObservePortfolioUseCase() } returns
                flowOf(Result.Success(listOf(oldPurchase, newPurchase)))

        // Act
        val viewModel = PortfolioViewModel(mockObservePortfolioUseCase, mockPortfolioRepository)

        // Assert
        viewModel.uiState.test {
            skipItems(1)
            val state = awaitItem() as PortfolioUiState.Success
            val btcGroup = state.holdings[0]

            Assert.assertEquals(2L, btcGroup.holdings[0].holding.id)
            Assert.assertEquals(1L, btcGroup.holdings[1].holding.id)
        }
    }
}
