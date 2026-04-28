package com.suminchoi.coachapp.design.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.suminchoi.coachapp.design.ZoneColors
import com.suminchoi.coachapp.design.rcColors

@Composable
fun AmbientBackground(modifier: Modifier = Modifier) {
    val colors = rcColors
    val recovery = ZoneColors.recovery.copy(alpha = if (colors.isDark) 0.12f else 0.10f)
    val threshold = ZoneColors.threshold.copy(alpha = if (colors.isDark) 0.09f else 0.07f)
    val long = ZoneColors.long.copy(alpha = if (colors.isDark) 0.10f else 0.08f)

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // top-left: recovery blue
            drawCircle(
                color = recovery,
                radius = size.width * 0.6f,
                center = Offset(x = size.width * 0.1f, y = size.height * 0.15f),
            )
            // top-right: threshold amber
            drawCircle(
                color = threshold,
                radius = size.width * 0.5f,
                center = Offset(x = size.width * 0.9f, y = size.height * 0.1f),
            )
            // bottom-center: long purple
            drawCircle(
                color = long,
                radius = size.width * 0.55f,
                center = Offset(x = size.width * 0.5f, y = size.height * 0.9f),
            )
        }
    }
}
