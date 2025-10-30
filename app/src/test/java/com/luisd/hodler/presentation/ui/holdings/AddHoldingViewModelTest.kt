package com.luisd.hodler.presentation.ui.holdings

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import app.cash.turbine.test
import com.luisd.hodler.domain.model.Coin
import com.luisd.hodler.domain.model.Holding
import com.luisd.hodler.domain.model.Result
import com.luisd.hodler.domain.repository.CoinRepository
import com.luisd.hodler.domain.repository.PortfolioRepository
import com.luisd.hodler.presentation.navigation.Screen
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddHoldingViewModelTest {
    private lateinit var mockCoinRepo: CoinRepository
    private lateinit var mockPortfolioRepo: PortfolioRepository

    private lateinit var savedStateHandle: SavedStateHandle

    private val testDispatcher = UnconfinedTestDispatcher()

    private val mockHolding = Holding(
        id = 1L,
        coinId = "bitcoin",
        coinSymbol = "BTC",
        coinName = "Bitcoin",
        amount = 0.5,
        purchasePrice = 45000.0,
        purchaseDate = System.currentTimeMillis(),
        imageUrl = ""
    )

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
        priceChangePercentage24h = 1.5,
        marketCap = 500000000L,
        marketCapRank = 2
    )

    private val mockCoins = listOf(mockBitcoin, mockEthereum)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockCoinRepo = mockk()
        mockPortfolioRepo = mockk()
        savedStateHandle = mockk(relaxed = true)
        mockkStatic("androidx.navigation.SavedStateHandleKt")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun setupAddMode() {
        every { savedStateHandle.toRoute<Screen.AddHoldingScreen>() } returns
                Screen.AddHoldingScreen(holdingId = null)
    }

    private fun setupEditMode(holdingId: Long) {
        every { savedStateHandle.toRoute<Screen.AddHoldingScreen>() } returns
                Screen.AddHoldingScreen(holdingId = holdingId)
    }

    @Test
    fun `initial state is coin selection with loading`() = runTest {
        // Arrange
        setupAddMode()
        coEvery { mockCoinRepo.getMarketCoins() } returns Result.Loading

        // Act
        val viewModel = AddHoldingViewModel(
            coinRepository = mockCoinRepo,
            portfolioRepository = mockPortfolioRepo,
            savedStateHandle = savedStateHandle,
        )

        // Assert
        assertEquals(AddHoldingUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `loading coins successfully transitions to coin selection`() = runTest {
        // Arrange
        setupAddMode()
        coEvery { mockCoinRepo.getMarketCoins() } returns Result.Success(
            data = mockCoins,
            isFromCache = false,
            lastUpdated = System.currentTimeMillis()
        )

        // Act
        val viewModel = AddHoldingViewModel(
            coinRepository = mockCoinRepo,
            portfolioRepository = mockPortfolioRepo,
            savedStateHandle = savedStateHandle,
        )

        // Assert
        val state = viewModel.uiState.value as AddHoldingUiState.CoinSelection
        assertEquals(mockCoins, state.coins)
        assertNull(state.error)
    }

    @Test
    fun `failing to load coins transitions to coin selection with error`() = runTest {
        // Arrange
        setupAddMode()
        coEvery { mockCoinRepo.getMarketCoins() } returns Result.Error(Exception("Failed"))

        // Act
        val viewModel = AddHoldingViewModel(
            coinRepository = mockCoinRepo,
            portfolioRepository = mockPortfolioRepo,
            savedStateHandle = savedStateHandle,
        )

        // Assert
        viewModel.uiState.test {
            val state = awaitItem() as AddHoldingUiState.CoinSelection
            assertEquals(listOf<Coin>(), state.coins)
            assertEquals("Unable to load coins", state.error)
        }
    }

    @Test
    fun `selecting coin transitions to form entry`() = runTest {
        // Arrange
        setupAddMode()
        coEvery { mockCoinRepo.getMarketCoins() } returns Result.Loading

        val viewModel = AddHoldingViewModel(
            coinRepository = mockCoinRepo,
            portfolioRepository = mockPortfolioRepo,
            savedStateHandle = savedStateHandle,
        )

        // Act
        viewModel.selectCoin(mockBitcoin)

        // Assert
        viewModel.uiState.test {
            val state = awaitItem() as AddHoldingUiState.FormEntry

            assertEquals(mockBitcoin, state.selectedCoin)
            assertEquals("", state.amount)
            assertFalse(state.isValid)
        }
    }

    @Test
    fun `save with empty amount shows error`() = runTest {
        // Arrange
        setupAddMode()
        coEvery { mockCoinRepo.getMarketCoins() } returns Result.Loading
        val viewModel = AddHoldingViewModel(
            coinRepository = mockCoinRepo,
            portfolioRepository = mockPortfolioRepo,
            savedStateHandle = savedStateHandle,
        )
        viewModel.selectCoin(mockBitcoin)
        viewModel.updateAmount("")
        viewModel.updatePurchasePrice("50000")

        // Act
        viewModel.saveHolding()

        // Assert
        val state = viewModel.uiState.value as AddHoldingUiState.FormEntry
        assertEquals("Amount is required", state.amountError)
        assertNull(state.priceError)
        assertFalse(state.isSaving)
        coVerify(exactly = 0) { mockPortfolioRepo.insertHolding(any()) }
    }

    @Test
    fun `save with negative price shows error`() = runTest {
        // Arrange
        setupAddMode()
        coEvery { mockCoinRepo.getMarketCoins() } returns Result.Loading
        val viewModel = AddHoldingViewModel(
            coinRepository = mockCoinRepo,
            portfolioRepository = mockPortfolioRepo,
            savedStateHandle = savedStateHandle,
        )
        viewModel.selectCoin(mockBitcoin)
        viewModel.updateAmount("1.0")
        viewModel.updatePurchasePrice("-100")

        // Act
        viewModel.saveHolding()

        // Assert
        val state = viewModel.uiState.value as AddHoldingUiState.FormEntry
        assertNull(state.amountError)
        assertEquals("Price must be positive", state.priceError)

        coVerify(exactly = 0) { mockPortfolioRepo.insertHolding(any()) }
    }

    @Test
    fun `save with valid data inserts holding`() = runTest {
        // Arrange
        setupAddMode()
        coEvery { mockCoinRepo.getMarketCoins() } returns Result.Loading
        coEvery { mockPortfolioRepo.insertHolding(any()) } returns Result.Success(
            data = 1L,
            isFromCache = false,
            lastUpdated = System.currentTimeMillis()
        )

        val viewModel = AddHoldingViewModel(
            coinRepository = mockCoinRepo,
            portfolioRepository = mockPortfolioRepo,
            savedStateHandle = savedStateHandle,
        )
        viewModel.selectCoin(mockBitcoin)
        viewModel.updateAmount("0.5")
        viewModel.updatePurchasePrice("48500")

        // Act
        viewModel.saveHolding()

        // Assert
        coVerify {
            mockPortfolioRepo.insertHolding(
                match {
                    it.coinId == "bitcoin" &&
                            it.amount == 0.5 &&
                            it.purchasePrice == 48500.0
                }
            )
        }
    }

    @Test
    fun `typing in field clears previous error`() = runTest {
        // Arrange
        setupAddMode()
        coEvery { mockCoinRepo.getMarketCoins() } returns Result.Loading

        val viewModel = AddHoldingViewModel(
            coinRepository = mockCoinRepo,
            portfolioRepository = mockPortfolioRepo,
            savedStateHandle = savedStateHandle,
        )
        viewModel.selectCoin(mockBitcoin)
        viewModel.updateAmount("")
        viewModel.saveHolding()

        val stateWithError = viewModel.uiState.value as AddHoldingUiState.FormEntry
        assertEquals("Amount is required", stateWithError.amountError)

        // User starts typing
        viewModel.updateAmount("0")

        // Error cleared
        val stateAfterTyping = viewModel.uiState.value as AddHoldingUiState.FormEntry
        assertNull(stateAfterTyping.amountError)
    }

    @Test
    fun `loading holding for edit transitions to form with pre-filled data`() = runTest {
        // Arrange
        setupEditMode(1L)
        coEvery { mockPortfolioRepo.getHoldingById(1L) } returns Result.Success(
            data = mockHolding,
            isFromCache = false,
            lastUpdated = System.currentTimeMillis()
        )

        // Act
        val viewModel = AddHoldingViewModel(
            coinRepository = mockCoinRepo,
            portfolioRepository = mockPortfolioRepo,
            savedStateHandle = savedStateHandle,
        )

        // Assert
        val state = viewModel.uiState.value as AddHoldingUiState.FormEntry

        assertEquals(mockHolding.coinId, state.selectedCoin.id)
        assertEquals(mockHolding.amount.toString(), state.amount)
        assertEquals(mockHolding.purchasePrice.toString(), state.purchasePrice)
        assertTrue(state.isEditMode)
        assertEquals(mockHolding.id, state.holdingId)
    }

    @Test
    fun `save in edit mode calls updateHolding instead of insertHolding`() = runTest {
        // Arrange
        setupEditMode(1L)
        coEvery { mockPortfolioRepo.getHoldingById(1L) } returns Result.Success(
            data = mockHolding,
            isFromCache = false,
            lastUpdated = System.currentTimeMillis()
        )
        coEvery { mockPortfolioRepo.updateHolding(any()) } returns Result.Success(
            data = Unit,
            isFromCache = false,
            lastUpdated = System.currentTimeMillis()
        )

        val viewModel = AddHoldingViewModel(
            coinRepository = mockCoinRepo,
            portfolioRepository = mockPortfolioRepo,
            savedStateHandle = savedStateHandle,
        )

        // Act
        viewModel.updateAmount("1.0")
        viewModel.updatePurchasePrice("48000")
        viewModel.saveHolding()

        // Assert
        coVerify { mockPortfolioRepo.updateHolding(any()) }
        coVerify(exactly = 0) { mockPortfolioRepo.insertHolding(any()) }
    }
}