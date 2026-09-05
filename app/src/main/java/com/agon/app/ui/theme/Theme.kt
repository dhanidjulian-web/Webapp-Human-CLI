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
    primary = Mint,
    onPrimary = DeepInk,
    primaryContainer = Color(0xFF004F53),
    onPrimaryContainer = SoftMint,
    secondary = Color(0xFF9FCFCA),
    secondaryContainer = Color(0xFF284A47),
    tertiary = WarmAmber,
    tertiaryContainer = Color(0xFF633B00),
    background = Night,
    surface = NightSurface,
    surfaceContainer = NightRaised,
    surfaceContainerLow = Color(0xFF111A19),
    error = Color(0xFFFFB4AB),
)

private val LightColorScheme = lightColorScheme(
    primary = InkBlue,
    onPrimary = Color.White,
    primaryContainer = PaleTeal,
    onPrimaryContainer = DeepInk,
    secondary = Color(0xFF496663),
    secondaryContainer = Color(0xFFCCE8E4),
    tertiary = Amber,
    tertiaryContainer = Color(0xFFFFDDB3),
    background = Cloud,
    surface = Color(0xFFFAFDFC),
    surfaceContainer = Color(0xFFE9F0EE),
    surfaceContainerLow = Color(0xFFF1F6F4),
    error = ErrorRed,
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
