import android.content.res.Configuration.UI_MODE_NIGHT_YES
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
import com.luisd.hodler.presentation.theme.HodlerTheme
import com.luisd.hodler.presentation.ui.components.LoadingContent
import com.luisd.hodler.presentation.ui.holdings.AddHoldingUiState
import com.luisd.hodler.presentation.ui.holdings.AddHoldingViewModel
import com.luisd.hodler.presentation.ui.holdings.components.CoinSelectionContent
import com.luisd.hodler.presentation.ui.holdings.components.HoldingFormContent
import com.luisd.hodler.presentation.ui.holdings.components.getSampleBitcoinForForm
import com.luisd.hodler.presentation.ui.holdings.components.getSampleCoinsForSelection
import com.luisd.hodler.presentation.ui.holdings.components.getSampleEthereumForForm

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
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                }

                is AddHoldingUiState.CoinSelection -> {
                    CoinSelectionContent(
                        coins = uiState.coins,
                        error = uiState.error,
                        onCoinSelected = onCoinSelected,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
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

// ============================================================
// AddHoldingScreen Previews
// ============================================================

@Preview(name = "Light: AddHolding - Loading State", showBackground = true)
@Preview(name = "Dark: AddHolding - Loading State", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AddHoldingScreenPreview_Loading() {
    HodlerTheme {
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

@Preview(name = "Light: AddHolding - Coin Selection State", showBackground = true)
@Preview(name = "Dark: AddHolding - Coin Selection State", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AddHoldingScreenPreview_CoinSelection() {
    HodlerTheme {
        AddHoldingScreen(
            uiState = AddHoldingUiState.CoinSelection(
                coins = getSampleCoinsForSelection()
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

@Preview(name = "Light: AddHolding - Error State", showBackground = true)
@Preview(name = "Dark: AddHolding - Error State", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AddHoldingScreenPreview_CoinSelectionError() {
    HodlerTheme {
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

@Preview(name = "Light: AddHolding - FormEntry State Add", showBackground = true)
@Preview(name = "Dark: AddHolding - FormEntry State Add", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AddHoldingScreenPreview_FormAdd() {
    HodlerTheme {
        AddHoldingScreen(
            uiState = AddHoldingUiState.FormEntry(
                selectedCoin = getSampleBitcoinForForm(),
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

@Preview(name = "Light: AddHolding - FormEntry State Edit", showBackground = true)
@Preview(name = "Dark: AddHolding - FormEntry State Edit", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AddHoldingScreenPreview_FormEdit() {
    HodlerTheme {
        AddHoldingScreen(
            uiState = AddHoldingUiState.FormEntry(
                selectedCoin = getSampleEthereumForForm(),
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

@Preview(name = "Light: AddHolding - FormEntry State with errors", showBackground = true)
@Preview(name = "Dark: AddHolding - FormEntry State with errors", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AddHoldingScreenPreview_FormWithErrors() {
    HodlerTheme {
        AddHoldingScreen(
            uiState = AddHoldingUiState.FormEntry(
                selectedCoin = getSampleBitcoinForForm(),
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

@Preview(name = "Light: AddHolding - FormEntry State saving", showBackground = true)
@Preview(name = "Dark: AddHolding - FormEntry State saving", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AddHoldingScreenPreview_FormSaving() {
    HodlerTheme {
        AddHoldingScreen(
            uiState = AddHoldingUiState.FormEntry(
                selectedCoin = getSampleBitcoinForForm(),
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

@Preview(name = "Light: AddHolding - FormEntry State save error", showBackground = true)
@Preview(name = "Dark: AddHolding - FormEntry State save error", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AddHoldingScreenPreview_FormSaveError() {
    HodlerTheme {
        AddHoldingScreen(
            uiState = AddHoldingUiState.FormEntry(
                selectedCoin = getSampleBitcoinForForm(),
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
