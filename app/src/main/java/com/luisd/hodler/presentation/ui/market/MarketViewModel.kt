package com.luisd.hodler.presentation.ui.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisd.hodler.domain.model.Coin
import com.luisd.hodler.domain.model.Result
import com.luisd.hodler.domain.repository.CoinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val repository: CoinRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<Result<List<Coin>>>(Result.Loading)
    val state: StateFlow<Result<List<Coin>>> = _state.asStateFlow()

    init {
        loadCoins()
    }

    private fun loadCoins() {
        viewModelScope.launch {
            repository.getMarketCoins().collect { result ->
                _state.value = result
            }
        }
    }

    fun refresh() {
        loadCoins()
    }
}