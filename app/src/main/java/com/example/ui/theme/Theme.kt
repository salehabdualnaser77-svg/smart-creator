package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SmartBlueLight,
    onPrimary = Color.White,
    primaryContainer = SmartBlueDark,
    onPrimaryContainer = Color.White,
    secondary = SmartCyanLight,
    onSecondary = Color.Black,
    secondaryContainer = DarkNavyCard,
    onSecondaryContainer = SmartCyanLight,
    tertiary = SmartYellow,
    background = DarkNavyBackground,
    surface = DarkNavySurface,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkNavyInput,
    outline = DarkNavyBorder
)

private val LightColorScheme = lightColorScheme(
    primary = SmartBlue,
    onPrimary = Color.White,
    primaryContainer = SmartBlueLight,
    onPrimaryContainer = Color.White,
    secondary = SmartCyan,
    onSecondary = Color.White,
    secondaryContainer = SmartCyanLight,
    tertiary = SmartYellow,
    background = LightBackground,
    surface = LightSurface,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFE2E8F0),
    outline = Color(0xFFCBD5E1)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

