package com.suminchoi.coachapp.design.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.suminchoi.coachapp.design.RcTypography
import com.suminchoi.coachapp.design.rcColors

@Composable
fun RcNavBar(
    title: String,
    visible: Boolean,
    modifier: Modifier = Modifier,
    actions: @Composable () -> Unit = {},
) {
    val colors = rcColors
    val bgColor = if (colors.isDark) Color(0xFF14213C).copy(alpha = 0.88f)
    else Color.White.copy(alpha = 0.88f)

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 220),
        label = "navAlpha",
    )

    if (alpha > 0f) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .alpha(alpha)
                .drawBehind {
                    drawRect(bgColor)
                    drawLine(
                        color = colors.border,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 0.5.dp.toPx(),
                    )
                }
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style = RcTypography.titleMedium,
                    color = colors.text,
                )
                Spacer(modifier = Modifier.weight(1f))
                actions()
            }
        }
    }
}
