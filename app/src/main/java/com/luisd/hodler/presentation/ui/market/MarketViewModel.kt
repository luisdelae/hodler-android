package com.luisd.hodler.presentation.ui.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisd.hodler.domain.model.Coin
import com.luisd.hodler.domain.model.Result
import com.luisd.hodler.domain.repository.CoinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketViewModel @Inject constructor(
    repository: CoinRepository,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val state: StateFlow<Result<List<Coin>>> = repository.getMarketCoins()
        .combine(_searchQuery.debounce(300)) { result, query ->
            when (result) {
                is Result.Success -> {
                    if (query.isBlank()) {
                        result
                    } else {
                        val filtered = result.data.filter { coin ->
                            coin.name.contains(query, ignoreCase = true) ||
                                    coin.symbol.contains(query, ignoreCase = true)
                        }
                        Result.Success(filtered)
                    }
                }

                is Result.Error -> result
                Result.Loading -> result
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.Loading
        )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onSearchActiveChange(active: Boolean) {
        _isSearchActive.value = active
        if (!active) {
            _searchQuery.value = ""
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _searchQuery.value = _searchQuery.value
        }
    }
}
