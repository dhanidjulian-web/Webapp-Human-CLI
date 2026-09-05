package com.agon.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = MusGoCyan,
    onPrimary = Color(0xFF00382F),
    primaryContainer = Color(0xFF004D40),
    onPrimaryContainer = MusGoCyan,
    secondary = MusGoBlue,
    onSecondary = Color(0xFF00344D),
    secondaryContainer = Color(0xFF0C4A6E),
    onSecondaryContainer = MusGoBlue,
    tertiary = MusGoAmber,
    onTertiary = Color(0xFF3D2E00),
    tertiaryContainer = Color(0xFF5C4500),
    onTertiaryContainer = MusGoAmber,
    error = MusGoRed,
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFECACA),
    background = MusGoBg,
    onBackground = MusGoOnBg,
    surface = MusGoSurface,
    onSurface = MusGoOnBg,
    surfaceVariant = MusGoSurfaceHigh,
    onSurfaceVariant = MusGoOnBgMuted,
    surfaceContainerLowest = MusGoBg,
    surfaceContainerLow = MusGoSurface,
    surfaceContainer = MusGoSurfaceHigh,
    surfaceContainerHigh = MusGoSurfaceHighest,
    surfaceContainerHighest = Color(0xFF2D3B52),
    outline = MusGoBorder,
    outlineVariant = Color(0xFF1E2A3A),
)

private val LightColorScheme = lightColorScheme(
    primary = MusGoPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCCFBF1),
    onPrimaryContainer = Color(0xFF134E4A),
    secondary = MusGoSecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0C4A6E),
    tertiary = Color(0xFFB45309),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFEF3C7),
    onTertiaryContainer = Color(0xFF78350F),
    error = Color(0xFFDC2626),
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),
    background = MusGoBgLight,
    onBackground = MusGoOnBgLight,
    surface = MusGoSurfaceLight,
    onSurface = MusGoOnBgLight,
    surfaceVariant = MusGoSurfaceHighLight,
    onSurfaceVariant = MusGoOnBgMutedLight,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF8FAFC),
    surfaceContainer = MusGoSurfaceHighLight,
    surfaceContainerHigh = Color(0xFFDCE4ED),
    surfaceContainerHighest = Color(0xFFD0DAE6),
    outline = Color(0xFF94A3B8),
    outlineVariant = Color(0xFFCBD5E1),
)

@Composable
fun AgonAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
