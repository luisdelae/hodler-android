package com.luisd.hodler.presentation.ui.portfolio.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.luisd.hodler.presentation.theme.HodlerTheme

@Composable
fun PortfolioEmptySection(
    onAddNewHolding: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Outlined.ShoppingBag,
            contentDescription = "Empty portfolio icon",
            modifier = Modifier.size(72.dp)
        )

        Text(
            text = "Start Building Your Portfolio",
            style = MaterialTheme.typography.titleMedium,
        )

        Text(
            text = "Track your crypto investments and watch your portfolio grow in real time",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        TextButton(
            onClick = { onAddNewHolding() },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.textButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = "Add your First coin",
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PortfolioEmptySectionPreview() {
    HodlerTheme {
        PortfolioEmptySection({})
    }
}