package com.luisd.hodler.presentation.ui.portfolio.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.luisd.hodler.domain.model.HoldingWithPrice
import com.luisd.hodler.presentation.ui.components.ProfitLossText
import com.luisd.hodler.presentation.ui.util.formatDate
import com.luisd.hodler.presentation.ui.util.toUsdFormat

@Composable
fun IndividualHoldingCard(
    holding: HoldingWithPrice,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 48.dp, bottom = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${holding.holding.amount} ${holding.holding.coinSymbol} at " +
                            holding.holding.purchasePrice.toUsdFormat(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    holding.currentValue.toUsdFormat(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = holding.holding.purchaseDate.formatDate(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                ProfitLossText(
                    profitLoss = holding.profitLoss,
                    profitLossPercent = holding.profitLossPercent,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
