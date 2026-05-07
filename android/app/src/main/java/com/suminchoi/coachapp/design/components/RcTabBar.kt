package com.suminchoi.coachapp.design.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.suminchoi.coachapp.design.Radius
import com.suminchoi.coachapp.design.RcTypography
import com.suminchoi.coachapp.design.rcColors

enum class RcTab { HOME, WEEKLY, TRENDS, GOALS, SETTINGS }

@Composable
fun RcTabBar(
    selectedTab: RcTab,
    onTabSelected: (RcTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = rcColors
    // 블러 없는 환경에서 가독성을 위해 높은 불투명도 사용
    val bgColor = if (colors.isDark) Color(0xFF0E1927).copy(alpha = 0.96f)
    else Color(0xFFFEFDF9).copy(alpha = 0.97f)
    val specularColor = if (colors.isDark) Color.White.copy(alpha = 0.12f)
    else Color.White.copy(alpha = 1.0f)
    val activePill = if (colors.isDark) Color.White.copy(alpha = 0.12f)
    else Color(0xFF0B1220).copy(alpha = 0.07f)

    val tabs = listOf(
        RcTab.HOME to "오늘",
        RcTab.WEEKLY to "주간",
        RcTab.TRENDS to "추이",
        RcTab.GOALS to "목표",
        RcTab.SETTINGS to "설정",
    )

    Box(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 12.dp)
            .navigationBarsPadding()
            .clip(RoundedCornerShape(Radius.tabBar))
            .drawBehind {
                drawRect(bgColor)
                // specular top highlight
                val highlightBrush = Brush.horizontalGradient(
                    0f to Color.Transparent,
                    0.3f to specularColor,
                    0.7f to specularColor,
                    1f to Color.Transparent,
                    startX = size.width * 0.14f,
                    endX = size.width * 0.86f,
                )
                drawRect(
                    brush = highlightBrush,
                    topLeft = Offset(0f, 0f),
                    size = androidx.compose.ui.geometry.Size(size.width, 1.dp.toPx()),
                )
            }
            .padding(horizontal = 6.dp, vertical = 7.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            tabs.forEach { (tab, label) ->
                val isSelected = tab == selectedTab
                val tint by animateColorAsState(
                    targetValue = if (isSelected) colors.text else colors.textMuted,
                    label = "tabTint_$tab",
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(22.dp))
                        .then(if (isSelected) Modifier.background(activePill) else Modifier)
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 0.dp, vertical = 7.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    RcTabIcon(tab = tab, tint = tint, active = isSelected)
                    Text(
                        text = label,
                        style = RcTypography.labelSmall,
                        color = tint,
                    )
                }
            }
        }
    }
}

@Composable
private fun RcTabIcon(tab: RcTab, tint: Color, active: Boolean) {
    val sw = if (active) 2f else 1.6f
    Box(
        modifier = Modifier
            .size(22.dp)
            .drawBehind {
                when (tab) {
                    RcTab.HOME -> drawHomeIcon(tint, sw, active)
                    RcTab.WEEKLY -> drawWeeklyIcon(tint, sw, active)
                    RcTab.TRENDS -> drawTrendsIcon(tint, sw, active)
                    RcTab.GOALS -> drawGoalsIcon(tint, sw, active)
                    RcTab.SETTINGS -> drawSettingsIcon(tint, sw, active)
                }
            },
    )
}

private fun DrawScope.drawHomeIcon(color: Color, sw: Float, active: Boolean) {
    val s = size.width
    val stroke = Stroke(width = sw.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    // house shape: M4 9.5L11 4l7 5.5V17.5a1 1 0 01-1 1h-3.5V13h-5v5.5H5a1 1 0 01-1-1V9.5z
    val scale = s / 22f
    val path = Path().apply {
        moveTo(4 * scale, 9.5f * scale)
        lineTo(11 * scale, 4 * scale)
        lineTo(18 * scale, 9.5f * scale)
        lineTo(18 * scale, 17.5f * scale)
        cubicTo(18 * scale, 18f * scale, 17.5f * scale, 18.5f * scale, 17 * scale, 18.5f * scale)
        lineTo(13.5f * scale, 18.5f * scale)
        lineTo(13.5f * scale, 13 * scale)
        lineTo(8.5f * scale, 13 * scale)
        lineTo(8.5f * scale, 18.5f * scale)
        lineTo(5 * scale, 18.5f * scale)
        cubicTo(4.5f * scale, 18.5f * scale, 4 * scale, 18f * scale, 4 * scale, 17.5f * scale)
        close()
    }
    if (active) drawPath(path, color.copy(alpha = 0.18f))
    drawPath(path, color, style = stroke)
}

private fun DrawScope.drawWeeklyIcon(color: Color, sw: Float, active: Boolean) {
    val s = size.width
    val scale = s / 22f
    val stroke = Stroke(width = sw.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    // calendar rect
    val rect = Path().apply {
        addRoundRect(
            androidx.compose.ui.geometry.RoundRect(
                left = 3 * scale, top = 4.5f * scale,
                right = 19 * scale, bottom = 19.5f * scale,
                radiusX = 2 * scale, radiusY = 2 * scale,
            )
        )
    }
    if (active) drawPath(rect, color.copy(alpha = 0.18f))
    drawPath(rect, color, style = stroke)
    // horizontal line at y=9
    drawLine(color, Offset(3 * scale, 9 * scale), Offset(19 * scale, 9 * scale), strokeWidth = sw.dp.toPx())
    // tick marks
    drawLine(color, Offset(7 * scale, 3 * scale), Offset(7 * scale, 6 * scale), strokeWidth = sw.dp.toPx(), cap = StrokeCap.Round)
    drawLine(color, Offset(15 * scale, 3 * scale), Offset(15 * scale, 6 * scale), strokeWidth = sw.dp.toPx(), cap = StrokeCap.Round)
}

private fun DrawScope.drawTrendsIcon(color: Color, sw: Float, active: Boolean) {
    val s = size.width
    val scale = s / 22f
    val stroke = Stroke(width = (if (active) 2.2f else 1.8f).dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    val linePath = Path().apply {
        moveTo(3 * scale, 16 * scale)
        lineTo(8 * scale, 11 * scale)
        lineTo(12 * scale, 14 * scale)
        lineTo(19 * scale, 6 * scale)
    }
    drawPath(linePath, color, style = stroke)
    val r = (if (active) 2f else 1.4f) * scale
    drawCircle(color, r, Offset(8 * scale, 11 * scale))
    drawCircle(color, r, Offset(12 * scale, 14 * scale))
    drawCircle(color, r, Offset(19 * scale, 6 * scale))
}

private fun DrawScope.drawGoalsIcon(color: Color, sw: Float, active: Boolean) {
    val s = size.width
    val scale = s / 22f
    val cx = 11 * scale
    val cy = 11 * scale
    val stroke = Stroke(width = sw.dp.toPx())
    drawCircle(color, 8 * scale, Offset(cx, cy), style = stroke)
    if (active) drawCircle(color.copy(alpha = 0.18f), 4 * scale, Offset(cx, cy))
    drawCircle(color, 4 * scale, Offset(cx, cy), style = stroke)
    drawCircle(color, (if (active) 1.8f else 1.2f) * scale, Offset(cx, cy))
}

private fun DrawScope.drawSettingsIcon(color: Color, sw: Float, active: Boolean) {
    val s = size.width
    val scale = s / 22f
    val cx = 11 * scale
    val cy = 11 * scale
    val stroke = Stroke(width = sw.dp.toPx(), cap = StrokeCap.Round)
    drawCircle(color, 3 * scale, Offset(cx, cy), style = stroke)
    if (active) drawCircle(color.copy(alpha = 0.18f), 3 * scale, Offset(cx, cy))
    // 8 spokes
    val spokeLength = 2.5f * scale
    val spokeStart = 5.2f * scale
    for (i in 0 until 8) {
        val angle = Math.toRadians(i * 45.0)
        val cos = Math.cos(angle).toFloat()
        val sin = Math.sin(angle).toFloat()
        drawLine(
            color,
            Offset(cx + cos * spokeStart, cy + sin * spokeStart),
            Offset(cx + cos * (spokeStart + spokeLength), cy + sin * (spokeStart + spokeLength)),
            strokeWidth = sw.dp.toPx(), cap = StrokeCap.Round,
        )
    }
}
