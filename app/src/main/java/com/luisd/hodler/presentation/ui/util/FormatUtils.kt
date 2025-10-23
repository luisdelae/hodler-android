package com.luisd.hodler.presentation.ui.util

import android.annotation.SuppressLint
import com.luisd.hodler.presentation.ui.details.TimeRange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("DefaultLocale")
fun Double.toUsdFormat(): String {
    return when {
        this >= 1 -> {
            // Format with 2 decimals and commas: $12,345.67
            "$${String.format("%,.2f", this)}"
        }
        this >= 0.01 -> {
            // Format with 4 decimals: $0.0123
            "$${String.format("%.4f", this)}"
        }
        else -> {
            // Format with 6 decimals for very small: $0.000123
            "$${String.format("%.6f", this)}"
        }
    }
}

fun Number.toCompactFormat() = when {
    toDouble() >= 1e12 -> "%.2fT".format(toDouble() / 1e12)
    toDouble() >= 1e9 -> "%.2fB".format(toDouble() / 1e9)
    toDouble() >= 1e6 -> "%.2fM".format(toDouble() / 1e6)
    toDouble() >= 1e3 -> "%.2fK".format(toDouble() / 1e3)
    else -> "%.2f".format(toDouble())
}

fun Double.toPercentageFormat(): String {
    val sign = if (this >= 0) "+" else ""
    return "$sign${"%.4f".format(this)}%"
}

fun Long.timeStampChartFormat(timeRange: TimeRange): String {
    val date = Date(this)
    val format = when (timeRange) {
        TimeRange.DAY_1 -> SimpleDateFormat("h:mm a", Locale.getDefault())
        TimeRange.DAY_7 -> SimpleDateFormat("EEE h a", Locale.getDefault())
        TimeRange.DAY_30 -> SimpleDateFormat("MMM d", Locale.getDefault())
        TimeRange.YEAR_1 -> SimpleDateFormat("MMM", Locale.getDefault())
    }
    return format.format(date)
}

fun Long.formatDate(): String {
    val date = Date(this)
    val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return format.format(date)
}