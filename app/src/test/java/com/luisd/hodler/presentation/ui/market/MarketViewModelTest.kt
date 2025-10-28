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
        viewModel.state.test {
            assertTrue(awaitItem() is Result.Loading)
            val success = awaitItem() as Result.Success
            assertEquals(mockCoins, success.data)
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
        viewModel.state.test {
            assertTrue(awaitItem() is Result.Loading)
            val error = awaitItem() as Result.Error
            assertEquals("Network error", error.exception.message)
        }
    }

    @Test
    fun `search query filters coins by name`() = runTest {
        // Arrange
        coEvery { mockRepository.getMarketCoins() } returns
                flowOf(Result.Success(mockCoins))
        val viewModel = MarketViewModel(mockRepository)

        // Act
        viewModel.onSearchQueryChange(query = "bitcoin")

        // Assert
        viewModel.state.test {
            assertTrue(awaitItem() is Result.Loading)
            val success = awaitItem() as Result.Success
            assertEquals(1, success.data.size)
            assertEquals("bitcoin", success.data[0].id)
        }
    }

    @Test
    fun `search query filters coins by symbol`() = runTest {
        // Arrange
        coEvery { mockRepository.getMarketCoins() } returns
                flowOf(Result.Success(mockCoins))
        val viewModel = MarketViewModel(mockRepository)

        // Act
        viewModel.onSearchQueryChange(query = "ETH")

        // Assert
        viewModel.state.test {
            assertTrue(awaitItem() is Result.Loading)
            val success = awaitItem() as Result.Success
            assertEquals(1, success.data.size)
            assertEquals("ethereum", success.data[0].id)
        }
    }

    @Test
    fun `search is case insensitive`() = runTest {
        // Arrange
        coEvery { mockRepository.getMarketCoins() } returns
                flowOf(Result.Success(mockCoins))
        val viewModel = MarketViewModel(mockRepository)

        // Act
        viewModel.onSearchQueryChange(query = "BITCOIN")

        // Assert
        viewModel.state.test {
            assertTrue(awaitItem() is Result.Loading)
            val success = awaitItem() as Result.Success
            assertEquals(1, success.data.size)
            assertEquals("bitcoin", success.data[0].id)
        }
    }

    @Test
    fun `empty search query shows all coins`() = runTest {
        // Arrange
        coEvery { mockRepository.getMarketCoins() } returns
                flowOf(Result.Success(mockCoins))
        val viewModel = MarketViewModel(mockRepository)

        // Act - Clear search
        viewModel.onSearchQueryChange(query = "bitcoin")
        viewModel.onSearchQueryChange(query = "")

        // Assert
        viewModel.state.test {
            assertTrue(awaitItem() is Result.Loading)
            val success = awaitItem() as Result.Success
            assertEquals(2, success.data.size)
            assertEquals("bitcoin", success.data[0].id)
            assertEquals("ethereum", success.data[1].id)
        }
    }

    @Test
    fun `search with no results returns empty list`() = runTest {
        // Arrange
        coEvery { mockRepository.getMarketCoins() } returns
                flowOf(Result.Success(mockCoins))
        val viewModel = MarketViewModel(mockRepository)

        // Act
        viewModel.onSearchQueryChange(query = "dogecoin")

        // Assert
        viewModel.state.test {
            assertTrue(awaitItem() is Result.Loading)
            val success = awaitItem() as Result.Success
            assertEquals(0, success.data.size)
        }
    }

    @Test
    fun `onSearchActiveChange clears query when deactivated`() = runTest {
        // Arrange
        coEvery { mockRepository.getMarketCoins() } returns
                flowOf(Result.Success(mockCoins))
        val viewModel = MarketViewModel(mockRepository)

        // Act
        viewModel.onSearchQueryChange(query = "bitcoin")
        viewModel.onSearchActiveChange(active = false)

        // Assert
        assertEquals("", viewModel.searchQuery.value)
    }
}
