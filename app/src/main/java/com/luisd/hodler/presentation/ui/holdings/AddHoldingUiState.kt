package com.luisd.hodler.presentation.ui.holdings

import com.luisd.hodler.domain.model.Coin

sealed interface AddHoldingUiState {

    data object Loading : AddHoldingUiState

    data class CoinSelection(
        val coins: List<Coin> = listOf(),
        val searchQuery: String = "",
        val isSearching: Boolean = false,
        val error: String? = null,
    ) : AddHoldingUiState

    data class FormEntry(
        val selectedCoin: Coin,
        val amount: String = "",
        val purchasePrice: String = "",
        val purchaseDate: Long = System.currentTimeMillis(),

        val amountError: String? = null,
        val priceError: String? = null,

        val isSaving: Boolean = false,
        val saveError: String? = null,

        val isEditMode: Boolean = false,
        val holdingId: Long? = null,
    ) : AddHoldingUiState {
        val isValid: Boolean
            get() = amount.isNotBlank()
                    && purchasePrice.isNotBlank()
                    && amountError == null
                    && priceError == null
    }

    data object SaveSuccess : AddHoldingUiState
}