package com.suminchoi.coachapp.design

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// TODO: Add pretendard_variable.ttf and jetbrains_mono_variable.ttf to res/font/
// then replace these with:
// val PretendardFamily = FontFamily(Font(R.font.pretendard_variable))
// val MonoFamily = FontFamily(Font(R.font.jetbrains_mono_variable))
val PretendardFamily: FontFamily = FontFamily.SansSerif
val MonoFamily: FontFamily = FontFamily.Monospace

object RcTypography {
    val displayLarge = TextStyle(
        fontFamily = PretendardFamily,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.8).sp,
    )
    val titleLarge = TextStyle(
        fontFamily = PretendardFamily,
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.4).sp,
    )
    val titleMedium = TextStyle(
        fontFamily = PretendardFamily,
        fontSize = 17.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.3).sp,
    )
    val bodyMedium = TextStyle(
        fontFamily = PretendardFamily,
        fontSize = 15.sp,
        fontWeight = FontWeight.Normal,
    )
    val bodySmall = TextStyle(
        fontFamily = PretendardFamily,
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal,
    )
    val labelSmall = TextStyle(
        fontFamily = PretendardFamily,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.2.sp,
    )
    val metricLarge = TextStyle(
        fontFamily = MonoFamily,
        fontSize = 48.sp,
        fontWeight = FontWeight.Medium,
    )
    val metricMedium = TextStyle(
        fontFamily = MonoFamily,
        fontSize = 32.sp,
        fontWeight = FontWeight.Medium,
    )
    val monoBody = TextStyle(
        fontFamily = MonoFamily,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
    )
}
