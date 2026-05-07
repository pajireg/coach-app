package com.suminchoi.coachapp.design.components

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.suminchoi.coachapp.design.ZoneColors
import com.suminchoi.coachapp.design.rcColors

@Composable
fun AmbientBackground(modifier: Modifier = Modifier) {
    val colors = rcColors
    val recovery  = ZoneColors.recovery.copy(alpha  = if (colors.isDark) 0.18f else 0.14f)
    val threshold = ZoneColors.threshold.copy(alpha = if (colors.isDark) 0.13f else 0.10f)
    val long      = ZoneColors.long.copy(alpha      = if (colors.isDark) 0.15f else 0.12f)

    Canvas(modifier = modifier.fillMaxSize()) {
        drawIntoCanvas { canvas ->
            val nc = canvas.nativeCanvas

            val recoveryPaint = android.graphics.Paint().apply {
                isAntiAlias = true
                color = recovery.toArgb()
                maskFilter = BlurMaskFilter(size.width * 0.38f, BlurMaskFilter.Blur.NORMAL)
            }
            nc.drawCircle(size.width * 0.1f, size.height * 0.15f, size.width * 0.62f, recoveryPaint)

            val thresholdPaint = android.graphics.Paint().apply {
                isAntiAlias = true
                color = threshold.toArgb()
                maskFilter = BlurMaskFilter(size.width * 0.32f, BlurMaskFilter.Blur.NORMAL)
            }
            nc.drawCircle(size.width * 0.9f, size.height * 0.10f, size.width * 0.52f, thresholdPaint)

            val longPaint = android.graphics.Paint().apply {
                isAntiAlias = true
                color = long.toArgb()
                maskFilter = BlurMaskFilter(size.width * 0.35f, BlurMaskFilter.Blur.NORMAL)
            }
            nc.drawCircle(size.width * 0.5f, size.height * 0.90f, size.width * 0.57f, longPaint)
        }
    }
}
