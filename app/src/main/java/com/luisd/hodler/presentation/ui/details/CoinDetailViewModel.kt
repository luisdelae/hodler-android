package com.luisd.hodler.presentation.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.luisd.hodler.domain.model.MarketChart
import com.luisd.hodler.domain.model.Result
import com.luisd.hodler.domain.repository.CoinRepository
import com.luisd.hodler.presentation.navigation.Screen
import com.luisd.hodler.presentation.ui.details.CoinDetailUiState.Error
import com.luisd.hodler.presentation.ui.details.CoinDetailUiState.Loading
import com.luisd.hodler.presentation.ui.details.CoinDetailUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    private val repository: CoinRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val args = savedStateHandle.toRoute<Screen.CoinDetail>()
    private val coinId: String = args.coinId
    val coinSymbol: String = args.coinSymbol

    private val _state = MutableStateFlow<CoinDetailUiState>(Loading)
    val state: StateFlow<CoinDetailUiState> = _state.asStateFlow()

    init {
        loadCoinDetails()
    }

    private fun loadCoinDetails() {
        viewModelScope.launch {
            repository.getCoinDetails(coinId).collect { result ->
                when (result) {
                    is Result.Error -> {
                        _state.value = Error(result.exception.message ?: "Unknown error")
                    }

                    Result.Loading -> _state.value = Loading
                    is Result.Success -> {
                        _state.value = Success(
                            coinDetail = result.data,
                            chartState = ChartState.Loading,
                            timeRange = TimeRange.DAY_7
                        )
                        loadMarketData(TimeRange.DAY_7)
                    }
                }
            }
        }
    }

    private fun loadMarketData(timeRange: TimeRange) {
        viewModelScope.launch {
            repository.getMarketChart(coinId = coinId, timeRange.days).collect { result ->
                _state.value = when (val currentState = _state.value) {
                    is Success -> currentState.copy(
                        chartState = when (result) {
                            is Result.Error -> ChartState.Error(
                                result.exception.message ?: "Chart error"
                            )

                            Result.Loading -> ChartState.Loading
                            is Result.Success<*> -> ChartState.Success(result.data as MarketChart)
                        },
                        timeRange = timeRange
                    )

                    else -> currentState // No update
                }
            }
        }
    }

    fun updateTimeRange(timeRange: TimeRange) {
        loadMarketData(timeRange)
    }
}
