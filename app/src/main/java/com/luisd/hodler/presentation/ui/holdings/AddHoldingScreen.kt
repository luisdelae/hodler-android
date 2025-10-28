import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.luisd.hodler.domain.model.Coin
import com.luisd.hodler.presentation.ui.components.LoadingContent
import com.luisd.hodler.presentation.ui.holdings.AddHoldingUiState
import com.luisd.hodler.presentation.ui.holdings.AddHoldingViewModel
import com.luisd.hodler.presentation.ui.holdings.components.CoinSelectionContent
import com.luisd.hodler.presentation.ui.holdings.components.HoldingFormContent

@Composable
fun AddHoldingRoute(
    onNavigateBack: () -> Unit,
    viewModel: AddHoldingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AddHoldingScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onCoinSelected = viewModel::selectCoin,
        onAmountChanged = viewModel::updateAmount,
        onPriceChanged = viewModel::updatePurchasePrice,
        onDateChanged = viewModel::updatePurchaseDate,
        onSaveClicked = viewModel::saveHolding,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddHoldingScreen(
    uiState: AddHoldingUiState,
    onNavigateBack: () -> Unit,
    onCoinSelected: (Coin) -> Unit,
    onAmountChanged: (String) -> Unit,
    onPriceChanged: (String) -> Unit,
    onDateChanged: (Long) -> Unit,
    onSaveClicked: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (uiState) {
                            is AddHoldingUiState.FormEntry ->
                                if (uiState.isEditMode) "Edit Holding" else "Add Holding"

                            else -> "Add Holding"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is AddHoldingUiState.Loading -> {
                    LoadingContent(
                        message = "Loading coins...",
                        paddingValues = paddingValues
                    )
                }

                is AddHoldingUiState.CoinSelection -> {
                    CoinSelectionContent(
                        coins = uiState.coins,
                        error = uiState.error,
                        paddingValues = paddingValues,
                        onCoinSelected = onCoinSelected,
                    )
                }

                is AddHoldingUiState.FormEntry -> {
                    HoldingFormContent(
                        state = uiState,
                        onAmountChanged = onAmountChanged,
                        onPriceChanged = onPriceChanged,
                        onDateChanged = onDateChanged,
                        onSaveClicked = onSaveClicked,
                    )
                }

                is AddHoldingUiState.SaveSuccess -> {
                    onNavigateBack()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddHoldingScreenPreview_Loading() {
    MaterialTheme {
        AddHoldingScreen(
            uiState = AddHoldingUiState.Loading,
            onNavigateBack = {},
            onCoinSelected = {},
            onAmountChanged = {},
            onPriceChanged = {},
            onDateChanged = {},
            onSaveClicked = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddHoldingScreenPreview_CoinSelection() {
    MaterialTheme {
        AddHoldingScreen(
            uiState = AddHoldingUiState.CoinSelection(
                coins = listOf(
                    Coin(
                        id = "bitcoin",
                        symbol = "BTC",
                        name = "Bitcoin",
                        image = "",
                        currentPrice = 54231.12,
                        priceChangePercentage24h = 2.5,
                        marketCap = 1000000000L,
                        marketCapRank = 1
                    ),
                    Coin(
                        id = "ethereum",
                        symbol = "ETH",
                        name = "Ethereum",
                        image = "",
                        currentPrice = 4012.58,
                        priceChangePercentage24h = -1.5,
                        marketCap = 500000000L,
                        marketCapRank = 2
                    ),
                    Coin(
                        id = "cardano",
                        symbol = "ADA",
                        name = "Cardano",
                        image = "",
                        currentPrice = 0.52,
                        priceChangePercentage24h = 3.2,
                        marketCap = 18000000L,
                        marketCapRank = 8
                    )
                )
            ),
            onNavigateBack = {},
            onCoinSelected = {},
            onAmountChanged = {},
            onPriceChanged = {},
            onDateChanged = {},
            onSaveClicked = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddHoldingScreenPreview_CoinSelectionError() {
    MaterialTheme {
        AddHoldingScreen(
            uiState = AddHoldingUiState.CoinSelection(
                coins = emptyList(),
                error = "Unable to load coins"
            ),
            onNavigateBack = {},
            onCoinSelected = {},
            onAmountChanged = {},
            onPriceChanged = {},
            onDateChanged = {},
            onSaveClicked = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddHoldingScreenPreview_FormAdd() {
    MaterialTheme {
        AddHoldingScreen(
            uiState = AddHoldingUiState.FormEntry(
                selectedCoin = Coin(
                    id = "bitcoin",
                    symbol = "BTC",
                    name = "Bitcoin",
                    image = "",
                    currentPrice = 54231.12,
                    priceChangePercentage24h = 2.5,
                    marketCap = 1000000000L,
                    marketCapRank = 1
                ),
                amount = "0.5",
                purchasePrice = "48500",
                purchaseDate = System.currentTimeMillis()
            ),
            onNavigateBack = {},
            onCoinSelected = {},
            onAmountChanged = {},
            onPriceChanged = {},
            onDateChanged = {},
            onSaveClicked = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddHoldingScreenPreview_FormEdit() {
    MaterialTheme {
        AddHoldingScreen(
            uiState = AddHoldingUiState.FormEntry(
                selectedCoin = Coin(
                    id = "ethereum",
                    symbol = "ETH",
                    name = "Ethereum",
                    image = "",
                    currentPrice = 4012.58,
                    priceChangePercentage24h = -1.5,
                    marketCap = 500000000L,
                    marketCapRank = 2
                ),
                amount = "2.5",
                purchasePrice = "3800",
                purchaseDate = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000, // 30 days ago
                isEditMode = true,
                holdingId = 1L
            ),
            onNavigateBack = {},
            onCoinSelected = {},
            onAmountChanged = {},
            onPriceChanged = {},
            onDateChanged = {},
            onSaveClicked = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddHoldingScreenPreview_FormWithErrors() {
    MaterialTheme {
        AddHoldingScreen(
            uiState = AddHoldingUiState.FormEntry(
                selectedCoin = Coin(
                    id = "bitcoin",
                    symbol = "BTC",
                    name = "Bitcoin",
                    image = "",
                    currentPrice = 54231.12,
                    priceChangePercentage24h = 2.5,
                    marketCap = 1000000000L,
                    marketCapRank = 1
                ),
                amount = "",
                purchasePrice = "-100",
                purchaseDate = System.currentTimeMillis(),
                amountError = "Amount is required",
                priceError = "Price must be positive"
            ),
            onNavigateBack = {},
            onCoinSelected = {},
            onAmountChanged = {},
            onPriceChanged = {},
            onDateChanged = {},
            onSaveClicked = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddHoldingScreenPreview_FormSaving() {
    MaterialTheme {
        AddHoldingScreen(
            uiState = AddHoldingUiState.FormEntry(
                selectedCoin = Coin(
                    id = "bitcoin",
                    symbol = "BTC",
                    name = "Bitcoin",
                    image = "",
                    currentPrice = 54231.12,
                    priceChangePercentage24h = 2.5,
                    marketCap = 1000000000L,
                    marketCapRank = 1
                ),
                amount = "0.5",
                purchasePrice = "48500",
                purchaseDate = System.currentTimeMillis(),
                isSaving = true
            ),
            onNavigateBack = {},
            onCoinSelected = {},
            onAmountChanged = {},
            onPriceChanged = {},
            onDateChanged = {},
            onSaveClicked = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddHoldingScreenPreview_FormSaveError() {
    MaterialTheme {
        AddHoldingScreen(
            uiState = AddHoldingUiState.FormEntry(
                selectedCoin = Coin(
                    id = "bitcoin",
                    symbol = "BTC",
                    name = "Bitcoin",
                    image = "",
                    currentPrice = 54231.12,
                    priceChangePercentage24h = 2.5,
                    marketCap = 1000000000L,
                    marketCapRank = 1
                ),
                amount = "0.5",
                purchasePrice = "48500",
                purchaseDate = System.currentTimeMillis(),
                saveError = "Failed to save: Database error"
            ),
            onNavigateBack = {},
            onCoinSelected = {},
            onAmountChanged = {},
            onPriceChanged = {},
            onDateChanged = {},
            onSaveClicked = {},
        )
    }
}
