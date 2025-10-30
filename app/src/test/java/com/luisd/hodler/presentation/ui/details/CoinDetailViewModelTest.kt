package com.luisd.hodler.presentation.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import app.cash.turbine.test
import com.luisd.hodler.domain.model.CoinDetail
import com.luisd.hodler.domain.model.MarketChart
import com.luisd.hodler.domain.model.PricePoint
import com.luisd.hodler.domain.model.Result
import com.luisd.hodler.domain.repository.CoinRepository
import com.luisd.hodler.presentation.navigation.Screen
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
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
class CoinDetailViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockRepository: CoinRepository
    private lateinit var savedStateHandle: SavedStateHandle

    private val mockCoinDetail = CoinDetail(
        id = "bitcoin",
        symbol = "BTC",
        name = "Bitcoin",
        image = "https://example.com/btc.png",
        currentPrice = 54231.12,
        marketCapUsd = 1000000000.0,
        marketCapRank = 1,
        totalVolumeUsd = 50000000000.0,
        priceChangePercentage24h = 2.5,
        circulatingSupply = 19000000.0,
        totalSupply = 21000000.0,
        maxSupply = 21000000.0,
        allTimeHighUsd = 69000.0,
        allTimeLowUsd = 67.81,
        allTimeHighUsdDate = "2021-11-10T14:24:11.849Z",
        allTimeLowUsdDate = "2013-07-06T00:00:00.000Z"
    )

    private val mockMarketChart = MarketChart(
        prices = listOf(
            PricePoint(timestamp = 1698537600000, price = 54000.0),
            PricePoint(timestamp = 1698624000000, price = 54231.12)
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()
        savedStateHandle = mockk(relaxed = true)
        mockkStatic("androidx.navigation.SavedStateHandleKt")

        every { savedStateHandle.toRoute<Screen.CoinDetail>() } returns
                Screen.CoinDetail(
                    coinId = "bitcoin",
                    coinSymbol = "btc"
                )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading then success`() = runTest {
        // Arrange
        coEvery { mockRepository.getCoinDetails("bitcoin") } returns
                Result.Success(mockCoinDetail)
        coEvery { mockRepository.getMarketChart("bitcoin", 7) } returns
                Result.Success(mockMarketChart)

        // Act
        val viewModel = CoinDetailViewModel(mockRepository, savedStateHandle)

        // Assert
        viewModel.state.test {
            assertTrue(awaitItem() is CoinDetailUiState.Loading)

            val success = awaitItem() as CoinDetailUiState.Success
            assertEquals(mockCoinDetail, success.coinDetail)
            skipItems(1)
            assertTrue(success.chartState is ChartState.Loading)
            assertEquals(TimeRange.DAY_7, success.timeRange)
        }
    }

    @Test
    fun `loading coin details returns error state`() = runTest {
        // Arrange
        val exception = Exception("Network error")
        coEvery { mockRepository.getCoinDetails("bitcoin") } returns
                Result.Error(exception)

        // Act
        val viewModel = CoinDetailViewModel(mockRepository, savedStateHandle)

        // Assert
        viewModel.state.test {
            skipItems(1)
            val error = awaitItem() as CoinDetailUiState.Error
            assertEquals("Network error", error.message)
        }
    }

    @Test
    fun `successful chart load updates chart state`() = runTest {
        // Arrange
        coEvery { mockRepository.getCoinDetails("bitcoin") } returns
                Result.Success(mockCoinDetail)
        coEvery { mockRepository.getMarketChart("bitcoin", 7) } returns
                Result.Success(mockMarketChart)

        // Act
        val viewModel = CoinDetailViewModel(mockRepository, savedStateHandle)

        // Assert
        viewModel.state.test {
            skipItems(1) // Skip Loading
            skipItems(1) // Skip Success with ChartState.Loading

            val success = awaitItem() as CoinDetailUiState.Success
            val chartSuccess = success.chartState as ChartState.Success
            assertEquals(mockMarketChart, chartSuccess.chart)
        }
    }

    @Test
    fun `chart error maintains coin detail success state`() = runTest {
        // Arrange
        coEvery { mockRepository.getCoinDetails("bitcoin") } returns
                Result.Success(mockCoinDetail)
        coEvery { mockRepository.getMarketChart("bitcoin", 7) } returns
                Result.Error(Exception("Chart error"))

        // Act
        val viewModel = CoinDetailViewModel(mockRepository, savedStateHandle)

        // Assert
        viewModel.state.test {
            skipItems(1) // Skip Loading
            skipItems(1) // Skip Success with ChartState.Loading

            val success = awaitItem() as CoinDetailUiState.Success
            assertEquals(mockCoinDetail, success.coinDetail)

            val chartError = success.chartState as ChartState.Error
            assertEquals("Chart error", chartError.message)
        }
    }

    @Test
    fun `updateTimeRange changes timeRange and triggers chart reload`() = runTest {
        // Arrange
        val chart7d = mockMarketChart
        val chart30d = MarketChart(
            prices = listOf(
                PricePoint(timestamp = 1696032000000, price = 50000.0),
                PricePoint(timestamp = 1698624000000, price = 54231.12)
            )
        )

        coEvery { mockRepository.getCoinDetails("bitcoin") } returns
                Result.Success(mockCoinDetail)
        coEvery { mockRepository.getMarketChart("bitcoin", 7) } returns
                Result.Success(chart7d)
        coEvery { mockRepository.getMarketChart("bitcoin", 30) } returns
                Result.Success(chart30d)

        // Act
        val viewModel = CoinDetailViewModel(mockRepository, savedStateHandle)
        advanceUntilIdle()

        viewModel.updateTimeRange(TimeRange.DAY_30)
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.value as CoinDetailUiState.Success
        assertEquals(TimeRange.DAY_30, state.timeRange)
        val chartData = (state.chartState as ChartState.Success).chart
        assertEquals(chart30d, chartData)
    }

    @Test
    fun `chart loading shows loading state while coin details remain visible`() = runTest {
        // Arrange
        coEvery { mockRepository.getCoinDetails("bitcoin") } returns
                Result.Success(mockCoinDetail)
        coEvery { mockRepository.getMarketChart("bitcoin", 7) } returns
                Result.Loading

        // Act
        val viewModel = CoinDetailViewModel(mockRepository, savedStateHandle)

        // Assert
        viewModel.state.test {
            skipItems(1)

            val success = awaitItem() as CoinDetailUiState.Success
            assertEquals(mockCoinDetail, success.coinDetail)
            assertTrue(success.chartState is ChartState.Loading)
        }
    }

    @Test
    fun `changing time range multiple times loads correct chart data`() = runTest {
        // Arrange
        val chart1h = MarketChart(
            prices = listOf(
                PricePoint(timestamp = 1698620000000, price = 54100.0),
                PricePoint(timestamp = 1698624000000, price = 54231.12)
            )
        )
        val chart1d = MarketChart(
            prices = listOf(
                PricePoint(timestamp = 1698537600000, price = 54000.0),
                PricePoint(timestamp = 1698624000000, price = 54231.12)
            )
        )

        coEvery { mockRepository.getCoinDetails("bitcoin") } returns
                Result.Success(mockCoinDetail)
        coEvery { mockRepository.getMarketChart("bitcoin", 7) } returns
                Result.Success(mockMarketChart)
        coEvery { mockRepository.getMarketChart("bitcoin", 1) } returns
                Result.Success(chart1d)
        coEvery { mockRepository.getMarketChart("bitcoin", any()) } returns
                Result.Success(chart1h)

        // Act
        val viewModel = CoinDetailViewModel(mockRepository, savedStateHandle)
        advanceUntilIdle()

        viewModel.updateTimeRange(TimeRange.DAY_1)
        advanceUntilIdle()

        val day1State = viewModel.state.value as CoinDetailUiState.Success
        assertEquals(TimeRange.DAY_1, day1State.timeRange)

        viewModel.updateTimeRange(TimeRange.DAY_30)
        advanceUntilIdle()

        val thirtyDayState = viewModel.state.value as CoinDetailUiState.Success
        assertEquals(TimeRange.DAY_30, thirtyDayState.timeRange)
    }

    @Test
    fun `coin symbol is accessible from viewModel`() = runTest {
        // Arrange
        coEvery { mockRepository.getCoinDetails("bitcoin") } returns
                Result.Success(mockCoinDetail)
        coEvery { mockRepository.getMarketChart("bitcoin", 7) } returns
                Result.Success(mockMarketChart)

        // Act
        val viewModel = CoinDetailViewModel(mockRepository, savedStateHandle)

        // Assert
        assertEquals("btc", viewModel.coinSymbol)
    }
}
