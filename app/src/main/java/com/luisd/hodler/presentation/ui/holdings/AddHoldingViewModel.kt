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

@HiltViewModel
class AddHoldingViewModel @Inject constructor(
    private val coinRepository: CoinRepository,
    private val portfolioRepository: PortfolioRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState = MutableStateFlow<AddHoldingUiState>(AddHoldingUiState.Loading)
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

    fun selectCoin(coin: Coin) {
        _uiState.value = AddHoldingUiState.FormEntry(selectedCoin = coin)
    }

    fun updateAmount(amount: String) {
        _uiState.update { state ->
            (state as? AddHoldingUiState.FormEntry)?.copy(
                amount = amount,
                amountError = null
            ) ?: state
        }
    }

    fun updatePurchasePrice(price: String) {
        _uiState.update { state ->
            (state as? AddHoldingUiState.FormEntry)?.copy(
                purchasePrice = price,
                priceError = null
            ) ?: state
        }
    }

    fun updatePurchaseDate(date: Long) {
        _uiState.update { state ->
            (state as? AddHoldingUiState.FormEntry)?.copy(
                purchaseDate = date
            ) ?: state
        }
    }

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

    private fun validateAmount(amount: String): String? {
        return when {
            amount.isBlank() -> "Amount is required"
            amount.toDoubleOrNull() == null -> "Amount must be a number"
            amount.toDouble() <= 0 -> "Amount must be positive"
            else -> null
        }
    }

    private fun validatePrice(price: String): String? {
        return when {
            price.isBlank() -> "Price is required"
            price.toDoubleOrNull() == null -> "Price must be a number"
            price.toDouble() <= 0 -> "Price must be positive"
            else -> null
        }
    }
}
