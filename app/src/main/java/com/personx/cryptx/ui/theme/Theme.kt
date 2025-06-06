package com.personx.cryptx.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// DARK MODE
val DarkColorScheme = darkColorScheme(
    primary = CyberpunkGreen,
    onPrimary = Color.Black,
    background = Color.Black,
    onBackground = CyberpunkGreen,
    surface = CyberSurfaceDark,
    onSurface = CyberpunkGreen,
    secondary = Color(0xFF00B67A), // deep green
    onSecondary = Color.White
)

// LIGHT MODE
val LightColorScheme = lightColorScheme(
    primary = CyberpunkGreen,
    onPrimary = Color.Black,
    background = CyberBackgroundLight,
    onBackground = Color.Black,
    surface = CyberSurfaceLight,
    onSurfaceVariant = CyberSurfaceLight,
    onSurface = CyberpunkGreen,
    secondary = Color(0xFF00B67A),
    onSecondary = Color.White
)

@Composable
fun CryptXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}