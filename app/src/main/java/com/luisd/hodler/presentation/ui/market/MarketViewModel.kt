package com.luisd.hodler.presentation.ui.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisd.hodler.domain.model.Result
import com.luisd.hodler.domain.repository.CoinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val repository: CoinRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MarketUiState>(MarketUiState.Loading)
    val uiState: StateFlow<MarketUiState> = _uiState.asStateFlow()

    init {
        loadCoins()
    }

    private fun loadCoins(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh && _uiState.value is MarketUiState.Success) {
                _uiState.update { (it as MarketUiState.Success).copy(isRefreshing = true) }
            }

            val result = repository.getMarketCoins()

            _uiState.value = when (result) {
                is Result.Success -> {
                    val currentState = _uiState.value
                    MarketUiState.Success(
                        coins = result.data,
                        searchQuery = (currentState as? MarketUiState.Success)?.searchQuery
                            ?: "",
                        isSearchActive = (currentState as? MarketUiState.Success)?.isSearchActive
                            ?: false,
                        isRefreshing = false
                    )
                }

                is Result.Error -> MarketUiState.Error(
                    message = result.exception.message ?: "Unknown error",
                    cachedCoins = (_uiState.value as? MarketUiState.Success)?.coins
                )

                Result.Loading -> if (isRefresh) _uiState.value else MarketUiState.Loading
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            if (state is MarketUiState.Success) {
                state.copy(searchQuery = query)
            } else state
        }
    }

    fun onSearchActiveChange(active: Boolean) {
        _uiState.update { state ->
            if (state is MarketUiState.Success) {
                state.copy(
                    isSearchActive = active,
                    searchQuery = if (!active) "" else state.searchQuery
                )
            } else state
        }
    }

    fun refresh() {
        loadCoins(isRefresh = true)
    }
}
