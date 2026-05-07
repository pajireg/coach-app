package com.suminchoi.coachapp.design.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.suminchoi.coachapp.design.Radius
import com.suminchoi.coachapp.design.rcColors

@Composable
fun RcCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = Radius.md,
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = rcColors
    val bgColor = if (colors.isDark) Color(0xFF1C263C).copy(alpha = 0.62f)
    else Color.White.copy(alpha = 0.72f)

    val shadowAlpha = if (colors.isDark) 0.35f else 0.08f
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 0.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = shadowAlpha),
                spotColor = Color.Black.copy(alpha = shadowAlpha),
            )
            .clip(shape)
            .drawBehind { drawRect(bgColor) }
            .border(
                width = 0.5.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = if (colors.isDark) 0.18f else 0.95f),
                        Color.White.copy(alpha = if (colors.isDark) 0.04f else 0.30f),
                    )
                ),
                shape = shape,
            )
            .padding(16.dp),
        content = content,
    )
}
