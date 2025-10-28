package com.luisd.hodler.presentation.ui.market

import app.cash.turbine.test
import com.luisd.hodler.domain.model.Coin
import com.luisd.hodler.domain.model.Result
import com.luisd.hodler.domain.repository.CoinRepository
import io.mockk.coEvery
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MarketViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockRepository: CoinRepository

    private val mockBitcoin = Coin(
        id = "bitcoin",
        symbol = "BTC",
        name = "Bitcoin",
        image = "",
        currentPrice = 54231.12,
        priceChangePercentage24h = 2.5,
        marketCap = 1000000000L,
        marketCapRank = 1
    )

    private val mockEthereum = Coin(
        id = "ethereum",
        symbol = "ETH",
        name = "Ethereum",
        image = "",
        currentPrice = 4012.58,
        priceChangePercentage24h = -1.5,
        marketCap = 500000000L,
        marketCapRank = 2
    )

    private val mockCoins = listOf(mockBitcoin, mockEthereum)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state shows loading then success`() = runTest {
        // Arrange
        coEvery { mockRepository.getMarketCoins() } returns
                flowOf(Result.Success(mockCoins))

        // Act
        val viewModel = MarketViewModel(mockRepository)

        // Assert
        viewModel.uiState.test {
            assertTrue(awaitItem() is MarketUiState.Loading)
            val success = awaitItem() as MarketUiState.Success
            assertEquals(mockCoins, success.coins)
            assertEquals("", success.searchQuery)
            assertFalse(success.isSearchActive)
            assertFalse(success.isRefreshing)
        }
    }

    @Test
    fun `loading coins returns error state`() = runTest {
        // Arrange
        coEvery { mockRepository.getMarketCoins() } returns
                flowOf(Result.Error(Exception("Network error")))

        // Act
        val viewModel = MarketViewModel(mockRepository)

        // Assert
        viewModel.uiState.test {
            assertTrue(awaitItem() is MarketUiState.Loading)
            val error = awaitItem() as MarketUiState.Error
            assertEquals("Network error", error.message)
        }
    }

    @Test
    fun `search query filters coins by name`() = runTest {
        // Arrange
        coEvery { mockRepository.getMarketCoins() } returns
                flowOf(Result.Success(mockCoins))
        val viewModel = MarketViewModel(mockRepository)
        advanceUntilIdle()

        // Act
        viewModel.onSearchQueryChange(query = "bitcoin")
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as MarketUiState.Success
        assertEquals("bitcoin", state.searchQuery)
        assertEquals(1, state.displayedCoins.size)
        assertEquals("bitcoin", state.displayedCoins[0].id)
    }

    @Test
    fun `search query filters coins by symbol`() = runTest {
        // Arrange
        coEvery { mockRepository.getMarketCoins() } returns
                flowOf(Result.Success(mockCoins))
        val viewModel = MarketViewModel(mockRepository)
        advanceUntilIdle()

        // Act
        viewModel.onSearchQueryChange(query = "ETH")
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as MarketUiState.Success
        assertEquals(1, state.displayedCoins.size)
        assertEquals("ethereum", state.displayedCoins[0].id)
    }

    @Test
    fun `search is case insensitive`() = runTest {
        // Arrange
        coEvery { mockRepository.getMarketCoins() } returns
                flowOf(Result.Success(mockCoins))
        val viewModel = MarketViewModel(mockRepository)
        advanceUntilIdle()

        // Act
        viewModel.onSearchQueryChange(query = "BITCOIN")
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as MarketUiState.Success
        assertEquals(1, state.displayedCoins.size)
        assertEquals("bitcoin", state.displayedCoins[0].id)
    }

    @Test
    fun `empty search query shows all coins`() = runTest {
        // Arrange
        coEvery { mockRepository.getMarketCoins() } returns
                flowOf(Result.Success(mockCoins))
        val viewModel = MarketViewModel(mockRepository)
        advanceUntilIdle()

        // Act - Filter then clear
        viewModel.onSearchQueryChange(query = "bitcoin")
        viewModel.onSearchQueryChange(query = "")
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as MarketUiState.Success
        assertEquals(2, state.displayedCoins.size)
        assertEquals("", state.searchQuery)
    }

    @Test
    fun `search with no results returns empty list`() = runTest {
        // Arrange
        coEvery { mockRepository.getMarketCoins() } returns
                flowOf(Result.Success(mockCoins))
        val viewModel = MarketViewModel(mockRepository)
        advanceUntilIdle()

        // Act
        viewModel.onSearchQueryChange(query = "dogecoin")
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as MarketUiState.Success
        assertEquals(0, state.displayedCoins.size)
        assertEquals(2, state.coins.size) // Original coins unchanged
    }

    @Test
    fun `onSearchActiveChange clears query when deactivated`() = runTest {
        // Arrange
        coEvery { mockRepository.getMarketCoins() } returns
                flowOf(Result.Success(mockCoins))
        val viewModel = MarketViewModel(mockRepository)
        advanceUntilIdle()

        // Act
        viewModel.onSearchQueryChange(query = "bitcoin")
        viewModel.onSearchActiveChange(active = false)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as MarketUiState.Success
        assertEquals("", state.searchQuery)
        assertFalse(state.isSearchActive)
    }

    @Test
    fun `refresh sets isRefreshing to true then false`() = runTest {
        // Arrange
        coEvery { mockRepository.getMarketCoins() } returns
                flowOf(Result.Success(mockCoins))
        val viewModel = MarketViewModel(mockRepository)
        advanceUntilIdle()

        // Act & Assert
        viewModel.uiState.test {
            skipItems(1) // Skip current success state

            viewModel.refresh()

            val refreshingState = awaitItem() as MarketUiState.Success
            assertTrue(refreshingState.isRefreshing)

            val completedState = awaitItem() as MarketUiState.Success
            assertFalse(completedState.isRefreshing)
            assertEquals(mockCoins, completedState.coins)
        }
    }

    @Test
    fun `refresh maintains search query and active state`() = runTest {
        // Arrange
        coEvery { mockRepository.getMarketCoins() } returns
                flowOf(Result.Success(mockCoins))
        val viewModel = MarketViewModel(mockRepository)
        advanceUntilIdle()

        viewModel.onSearchQueryChange("bitcoin")
        viewModel.onSearchActiveChange(true)
        advanceUntilIdle()

        // Act
        viewModel.refresh()
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as MarketUiState.Success
        assertEquals("bitcoin", state.searchQuery)
        assertTrue(state.isSearchActive)
        assertEquals(1, state.displayedCoins.size)
    }

    @Test
    fun `displayedCoins returns all coins when search query is blank`() = runTest {
        // Arrange
        coEvery { mockRepository.getMarketCoins() } returns
                flowOf(Result.Success(mockCoins))
        val viewModel = MarketViewModel(mockRepository)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as MarketUiState.Success
        assertEquals(mockCoins, state.displayedCoins)
    }

    @Test
    fun `displayedCoins filters when search query is set`() = runTest {
        // Arrange
        coEvery { mockRepository.getMarketCoins() } returns
                flowOf(Result.Success(mockCoins))
        val viewModel = MarketViewModel(mockRepository)
        advanceUntilIdle()

        // Act
        viewModel.onSearchQueryChange("eth")
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as MarketUiState.Success
        assertEquals(1, state.displayedCoins.size)
        assertEquals("ethereum", state.displayedCoins[0].id)
    }
}
