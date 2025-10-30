package com.luisd.hodler.presentation.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.luisd.hodler.presentation.theme.HodlerTheme

/**
 * Banner that indicates data is being shown from cache (offline mode).
 * Displays a timestamp of when data was last updated and optional retry button.
 *
 * @param lastUpdated Timestamp in milliseconds when data was last fetched, null for generic "Cached data" message
 * @param onRefresh Callback when retry button is clicked, null to hide retry button
 * @param modifier Modifier for the banner surface
 */
@Composable
fun CacheIndicatorBanner(
    lastUpdated: Long?,
    onRefresh: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CloudOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = buildCacheMessage(lastUpdated),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            if (onRefresh != null) {
                TextButton(
                    onClick = onRefresh,
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(
                        text = "Retry",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Preview(name = "Light: CacheIndicationBanner - Time", showBackground = true)
@Preview(
    name = "Dark: CacheIndicationBanner",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewCacheIndicatorBanner_Time() {
    HodlerTheme {
        CacheIndicatorBanner(
            lastUpdated = System.currentTimeMillis() - 80_0000,
            onRefresh = { },
            modifier = Modifier
        )
    }
}

@Preview(name = "Light: CacheIndicationBanner - No time", showBackground = true)
@Preview(
    name = "Dark: CacheIndicationBanner",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewCacheIndicatorBanner_NoTime() {
    HodlerTheme {
        CacheIndicatorBanner(
            lastUpdated = null,
            onRefresh = { },
            modifier = Modifier
        )
    }
}

/**
 * Builds the cache message text based on the last updated timestamp.
 *
 * @param lastUpdated Timestamp in milliseconds, null for generic message
 * @return Formatted message like "Offline - 5m ago" or "Offline - Cached data"
 */
private fun buildCacheMessage(lastUpdated: Long?): String {
    return if (lastUpdated != null) {
        "Offline - ${formatRelativeTime(lastUpdated)}"
    } else {
        "Offline - Cached data"
    }
}

/**
 * Formats a timestamp into a relative time string.
 *
 * @param timestamp Timestamp in milliseconds
 * @return Relative time string like "Just now", "5m ago", "2h ago", "3d ago"
 */
private fun formatRelativeTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    return when {
        diff < 60_000 -> "Just now"
        diff < 3600_000 -> "${diff / 60_000}m ago"
        diff < 86400_000 -> "${diff / 3600_000}h ago"
        else -> "${diff / 86400_000}d ago"
    }
}