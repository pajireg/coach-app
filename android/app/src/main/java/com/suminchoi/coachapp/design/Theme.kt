package com.suminchoi.coachapp.design

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val LightColorScheme = lightColorScheme(
    background = LightRcColors.bg,
    surface = LightRcColors.bgElev,
    onBackground = LightRcColors.text,
    onSurface = LightRcColors.text,
)

private val DarkColorScheme = darkColorScheme(
    background = DarkRcColors.bg,
    surface = DarkRcColors.bgElev,
    onBackground = DarkRcColors.text,
    onSurface = DarkRcColors.text,
)

@Composable
fun CoachAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val rcColors = if (darkTheme) DarkRcColors else LightRcColors
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalRcColors provides rcColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )
    }
}
