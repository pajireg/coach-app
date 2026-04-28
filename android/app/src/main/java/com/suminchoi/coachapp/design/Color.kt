package com.suminchoi.coachapp.design

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

data class RcColors(
    val bg: Color,
    val bgElev: Color,
    val bgElev2: Color,
    val text: Color,
    val textDim: Color,
    val textFaint: Color,
    val textMuted: Color,
    val border: Color,
    val borderStrong: Color,
    val isDark: Boolean,
)

val LightRcColors = RcColors(
    bg = Color(0xFFF2EFE8),
    bgElev = Color(0xFFFFFFFF),
    bgElev2 = Color(0xFFFAF8F3),
    text = Color(0xFF0B1220),
    textDim = Color(0xFF0B1220).copy(alpha = 0.68f),
    textFaint = Color(0xFF0B1220).copy(alpha = 0.46f),
    textMuted = Color(0xFF0B1220).copy(alpha = 0.30f),
    border = Color(0xFF0B1220).copy(alpha = 0.08f),
    borderStrong = Color(0xFF0B1220).copy(alpha = 0.16f),
    isDark = false,
)

val DarkRcColors = RcColors(
    bg = Color(0xFF0B1220),
    bgElev = Color(0xFF121A2B),
    bgElev2 = Color(0xFF1A2236),
    text = Color(0xFFF5F3EE),
    textDim = Color(0xFFF5F3EE).copy(alpha = 0.72f),
    textFaint = Color(0xFFF5F3EE).copy(alpha = 0.50f),
    textMuted = Color(0xFFF5F3EE).copy(alpha = 0.32f),
    border = Color.White.copy(alpha = 0.10f),
    borderStrong = Color.White.copy(alpha = 0.18f),
    isDark = true,
)

val LocalRcColors = compositionLocalOf { LightRcColors }
val rcColors: RcColors @Composable get() = LocalRcColors.current

object ZoneColors {
    val recovery  = Color(0xFF4A9EDB)
    val base      = Color(0xFF3FB87F)
    val threshold = Color(0xFFE8A94C)
    val interval  = Color(0xFFE35D5D)
    val rest      = Color(0xFF8A8F99)
    val long      = Color(0xFF8F7BD4)
}
