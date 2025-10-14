package com.luisd.hodler.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    // Primary - Deep Blue (navigation, key actions)
    primary = CryptoBlueLight,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = CryptoBlue,
    onPrimaryContainer = Color(0xFFD1E3FF),

    // Secondary - Gold/Amber (accents, highlights)
    secondary = CryptoGold,
    onSecondary = Color(0xFF000000),
    secondaryContainer = CryptoAmber,
    onSecondaryContainer = Color(0xFFFFEDB3),

    // Tertiary - Cyan (links, info)
    tertiary = CryptoCyan,
    onTertiary = Color(0xFF000000),
    tertiaryContainer = Color(0xFF0E7490),
    onTertiaryContainer = Color(0xFFCFFAFE),

    // Error - Red (losses, errors)
    error = LossRed,
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2),

    // Background & Surface
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,

    // Outline
    outline = BorderDark,
    outlineVariant = Color(0xFF475569),
)

private val LightColorScheme = lightColorScheme(
    // Primary - Deep Blue
    primary = CryptoBlue,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = CryptoBlueLight,
    onPrimaryContainer = Color(0xFF001D36),

    // Secondary - Gold/Amber
    secondary = CryptoAmber,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = CryptoGold,
    onSecondaryContainer = Color(0xFF291800),

    // Tertiary - Cyan
    tertiary = Color(0xFF0E7490),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = CryptoCyan,
    onTertiaryContainer = Color(0xFF001F24),

    // Error - Red
    error = ErrorRed,
    onError = Color(0xFFFFFFFF),
    errorContainer = LossRedLight,
    onErrorContainer = Color(0xFF410002),

    // Background & Surface
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,

    // Outline
    outline = BorderLight,
    outlineVariant = Color(0xFFE2E8F0),
)

@Composable
fun HodlerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    // Set to false to use custom crypto theme always
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
