package com.luisd.hodler.presentation.ui

import android.annotation.SuppressLint

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
