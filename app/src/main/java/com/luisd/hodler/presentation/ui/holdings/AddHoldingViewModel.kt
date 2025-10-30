package com.luisd.hodler.presentation.ui.holdings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.luisd.hodler.domain.model.Coin
import com.luisd.hodler.domain.model.Holding
import com.luisd.hodler.domain.model.Result
import com.luisd.hodler.domain.repository.CoinRepository
import com.luisd.hodler.domain.repository.PortfolioRepository
import com.luisd.hodler.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Add/Edit Holding screen that manages portfolio entry creation and modification.
 *
 * Supports three entry modes:
 * 1. Add new holding - User selects coin from list, then fills form
 * 2. Edit existing holding - Loads holding by ID and populates form for editing
 * 3. Quick add from coin detail - Preselects coin and goes directly to form
 *
 * Responsibilities:
 * - Manages multi-step flow (coin selection → form entry)
 * - Validates form input (amount and price must be positive numbers)
 * - Handles both insert (new) and update (edit) operations
 * - Provides real-time validation feedback
 *
 * Entry mode is determined by navigation arguments:
 * - holdingId present → Edit mode
 * - coinId present → Quick add mode
 * - Neither present → Full add mode (select coin first)
 *
 * @property coinRepository Repository for fetching available cryptocurrencies
 * @property portfolioRepository Repository for CRUD operations on holdings
 * @property savedStateHandle Contains navigation arguments (holdingId and coinId)
 */
@HiltViewModel
class AddHoldingViewModel @Inject constructor(
    private val coinRepository: CoinRepository,
    private val portfolioRepository: PortfolioRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState = MutableStateFlow<AddHoldingUiState>(AddHoldingUiState.Loading)

    /**
     * UI state flow representing the current add/edit holding screen state.
     *
     * States:
     * - Loading: Fetching initial data (coins list or holding to edit)
     * - CoinSelection: Displaying list of coins for user to select
     * - FormEntry: Form for entering amount, purchase price, and date
     * - SaveSuccess: Holding successfully saved, ready to navigate back
     */
    val uiState: StateFlow<AddHoldingUiState> = _uiState.asStateFlow()

    private val route = savedStateHandle.toRoute<Screen.AddHoldingScreen>()
    private val holdingIdToEdit = route.holdingId
    private val preselectedCoinId = route.coinId

    init {
        when {
            holdingIdToEdit != null -> loadHoldingForEdit(holdingIdToEdit)
            preselectedCoinId != null -> loadPreselectedCoin(preselectedCoinId)
            else -> loadCoins()
        }
    }

    /**
     * Loads the list of available cryptocurrencies for selection.
     *
     * Used in full add mode when user needs to select which coin to add to portfolio.
     */
    private fun loadCoins() {
        viewModelScope.launch {
            val result = coinRepository.getMarketCoins()
            _uiState.value = when (result) {
                is Result.Success -> AddHoldingUiState.CoinSelection(coins = result.data)
                is Result.Error -> AddHoldingUiState.CoinSelection(error = "Unable to load coins")
                Result.Loading -> AddHoldingUiState.Loading
            }
        }
    }

    /**
     * Loads an existing holding for editing.
     *
     * Fetches the holding from the database and populates the form with its current values.
     * Used in edit mode when user taps edit action on an existing holding.
     *
     * @param holdingId Unique identifier of the holding to edit
     */
    private fun loadHoldingForEdit(holdingId: Long) {
        viewModelScope.launch {
            _uiState.value = AddHoldingUiState.Loading

            val result = portfolioRepository.getHoldingById(holdingId)
            when (result) {
                is Result.Success -> {
                    val holding = result.data
                    _uiState.value = AddHoldingUiState.FormEntry(
                        selectedCoin = Coin(
                            id = holding.coinId,
                            symbol = holding.coinSymbol,
                            name = holding.coinName,
                            image = holding.imageUrl,
                            currentPrice = holding.purchasePrice,
                            priceChangePercentage24h = 0.0,
                            marketCap = 0L,
                            marketCapRank = 0
                        ),
                        amount = holding.amount.toString(),
                        purchasePrice = holding.purchasePrice.toString(),
                        purchaseDate = holding.purchaseDate,
                        isEditMode = true,
                        holdingId = holding.id
                    )
                }

                is Result.Error -> {
                    _uiState.value = AddHoldingUiState.CoinSelection(
                        error = "Failed to load holding: ${result.exception.message}"
                    )
                }

                Result.Loading -> { /* Won't happen */ }
            }
        }
    }

    /**
     * Loads a preselected coin for quick add flow.
     *
     * Used when navigating from coin detail screen with "Add to Portfolio" button.
     * Fetches coin data and goes directly to form entry, skipping coin selection.
     *
     * Falls back to full coin selection if fetching the preselected coin fails.
     *
     * @param coinId Unique identifier of the coin to add
     */
    private fun loadPreselectedCoin(coinId: String) {
        viewModelScope.launch {
            val result = coinRepository.getCoinById(coinId)
            when (result) {
                is Result.Success -> {
                    _uiState.value = AddHoldingUiState.FormEntry(
                        selectedCoin = result.data
                    )
                }

                is Result.Error -> {
                    loadCoins()
                }

                Result.Loading -> {
                    _uiState.value = AddHoldingUiState.Loading
                }
            }
        }
    }

    /**
     * Selects a coin and advances to the form entry screen.
     *
     * Called when user taps a coin in the coin selection list.
     *
     * @param coin The selected cryptocurrency to add to portfolio
     */
    fun selectCoin(coin: Coin) {
        _uiState.value = AddHoldingUiState.FormEntry(selectedCoin = coin)
    }


    /**
     * Updates the amount field in the form.
     *
     * Clears any previous validation error for amount when user types.
     *
     * @param amount String representation of the amount to hold
     */
    fun updateAmount(amount: String) {
        _uiState.update { state ->
            (state as? AddHoldingUiState.FormEntry)?.copy(
                amount = amount,
                amountError = null
            ) ?: state
        }
    }

    /**
     * Updates the purchase price field in the form.
     *
     * Clears any previous validation error for price when user types.
     *
     * @param price String representation of the purchase price per unit
     */
    fun updatePurchasePrice(price: String) {
        _uiState.update { state ->
            (state as? AddHoldingUiState.FormEntry)?.copy(
                purchasePrice = price,
                priceError = null
            ) ?: state
        }
    }

    /**
     * Updates the purchase date in the form.
     *
     * @param date Purchase date as timestamp in milliseconds
     */
    fun updatePurchaseDate(date: Long) {
        _uiState.update { state ->
            (state as? AddHoldingUiState.FormEntry)?.copy(
                purchaseDate = date
            ) ?: state
        }
    }

    /**
     * Validates and saves the holding to the portfolio.
     *
     * Performs validation on amount and price fields before saving.
     * If validation fails, displays inline error messages on the form.
     * If validation passes, performs insert (new holding) or update (edit mode).
     *
     * On successful save, transitions to SaveSuccess state which triggers navigation back.
     */
    fun saveHolding() {
        val currentState = _uiState.value as? AddHoldingUiState.FormEntry ?: return

        val amountError = validateAmount(currentState.amount)
        val priceError = validatePrice(currentState.purchasePrice)

        if (amountError != null || priceError != null) {
            _uiState.value = currentState.copy(
                amountError = amountError,
                priceError = priceError
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isSaving = true)

            val holding = Holding(
                id = currentState.holdingId ?: 0L,
                coinId = currentState.selectedCoin.id,
                coinSymbol = currentState.selectedCoin.symbol,
                coinName = currentState.selectedCoin.name,
                amount = currentState.amount.toDouble(),
                purchasePrice = currentState.purchasePrice.toDouble(),
                purchaseDate = currentState.purchaseDate,
                imageUrl = currentState.selectedCoin.image
            )

            val result = if (currentState.isEditMode) {
                portfolioRepository.updateHolding(holding)
            } else {
                portfolioRepository.insertHolding(holding)
            }

            when (result) {
                is Result.Success -> {
                    _uiState.update {
                        AddHoldingUiState.SaveSuccess
                    }
                }

                is Result.Error -> {
                    _uiState.update {
                        currentState.copy(
                            isSaving = false,
                            saveError = "Failed to save: ${result.exception.message}"
                        )
                    }
                }

                Result.Loading -> { /* Won't happen */ }
            }
        }
    }

    /**
     * Validates the amount field.
     *
     * @param amount String to validate
     * @return Error message if invalid, null if valid
     */
    private fun validateAmount(amount: String): String? {
        return when {
            amount.isBlank() -> "Amount is required"
            amount.toDoubleOrNull() == null -> "Amount must be a number"
            amount.toDouble() <= 0 -> "Amount must be positive"
            else -> null
        }
    }

    /**
     * Validates the purchase price field.
     *
     * @param price String to validate
     * @return Error message if invalid, null if valid
     */
    private fun validatePrice(price: String): String? {
        return when {
            price.isBlank() -> "Price is required"
            price.toDoubleOrNull() == null -> "Price must be a number"
            price.toDouble() <= 0 -> "Price must be positive"
            else -> null
        }
    }
}
