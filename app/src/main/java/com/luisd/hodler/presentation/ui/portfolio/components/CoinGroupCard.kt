package com.luisd.hodler.presentation.ui.portfolio.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.luisd.hodler.presentation.ui.components.ProfitLossPercentText
import com.luisd.hodler.presentation.ui.portfolio.CoinGroup
import com.luisd.hodler.presentation.ui.util.toUsdFormat

@Composable
fun CoinGroupCard(
    coinGroup: CoinGroup,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = coinGroup.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(coinGroup.coinName, style = MaterialTheme.typography.titleMedium)
                    if (coinGroup.holdingCount > 1) {
                        Text(
                            " (${coinGroup.holdingCount} purchases)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(coinGroup.coinSymbol, style = MaterialTheme.typography.bodyMedium)
                Text(
                    "${coinGroup.totalAmount} ${coinGroup.coinSymbol}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    coinGroup.totalCurrentValue.toUsdFormat(),
                    style = MaterialTheme.typography.titleMedium
                )
                ProfitLossPercentText(coinGroup.totalProfitLossPercent)
            }

            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Collapse" else "Expand"
            )
        }
    }
}
