package com.musicai.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF6C5CE7),
    secondary = Color(0xFF1DA1F2),
    background = Color(0xFF0B0B10),
    surface = Color(0xFF141421),
    onPrimary = Color.White,
    onBackground = Color(0xFFEDEDF1),
    onSurface = Color(0xFFEDEDF1)
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF6C5CE7),
    secondary = Color(0xFF1DA1F2),
    background = Color(0xFFF7F8FC),
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color(0xFF101018),
    onSurface = Color(0xFF101018)
)

@Composable
fun MelodyMindTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
