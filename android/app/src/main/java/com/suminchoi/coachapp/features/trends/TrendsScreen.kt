package com.suminchoi.coachapp.features.trends

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.suminchoi.coachapp.R
import com.suminchoi.coachapp.core.model.PaceTrendPoint
import com.suminchoi.coachapp.core.model.TrendWeek
import com.suminchoi.coachapp.core.model.TrendsResponse
import com.suminchoi.coachapp.core.model.formatPace
import com.suminchoi.coachapp.design.RcTypography
import com.suminchoi.coachapp.design.Spacing
import com.suminchoi.coachapp.design.ZoneColors
import com.suminchoi.coachapp.design.components.RcCard
import com.suminchoi.coachapp.design.components.RcScreen
import com.suminchoi.coachapp.design.components.RcSectionHeader
import com.suminchoi.coachapp.design.rcColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendsScreen(viewModel: TrendsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PullToRefreshBox(
        isRefreshing = state is TrendsUiState.Loading,
        onRefresh = viewModel::load,
    ) {
        RcScreen(
            title = stringResource(R.string.tab_trends),
            eyebrow = stringResource(R.string.trends_eyebrow),
        ) {
            when (val s = state) {
                is TrendsUiState.Loading -> item {
                    Box(modifier = Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is TrendsUiState.Error -> item {
                    Box(modifier = Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
                        Text(s.message, style = RcTypography.bodyMedium, color = rcColors.textDim)
                    }
                }

                is TrendsUiState.Success -> {
                    item { TrendsSummaryCard(s.trends) }
                    item { RcSectionHeader(title = stringResource(R.string.trends_weekly_volume)) }
                    item { WeeklyVolumeChart(s.trends.weeklyVolume) }
                    item { RcSectionHeader(title = stringResource(R.string.trends_pace)) }
                    item { PaceTrendCard(s.trends.paceTrend) }
                }
            }
        }
    }
}

@Composable
private fun TrendsSummaryCard(trends: TrendsResponse) {
    RcCard(
        modifier = Modifier.padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.lg)) {
            MetricBlock(
                label = stringResource(R.string.trends_acwr),
                value = trends.acwr?.let { "%.2f".format(it) } ?: "--",
                modifier = Modifier.weight(1f),
            )
            MetricBlock(
                label = stringResource(R.string.trends_as_of),
                value = trends.asOf.ifBlank { "--" },
                valueStyle = RcTypography.monoBody,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun WeeklyVolumeChart(weeks: List<TrendWeek>) {
    RcCard(
        modifier = Modifier.padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm),
    ) {
        if (weeks.isEmpty()) {
            EmptyState(text = stringResource(R.string.trends_empty_weekly))
            return@RcCard
        }

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
            MetricBlock(
                label = stringResource(R.string.trends_latest_week),
                value = "%.1fkm".format(weeks.last().distanceKm),
            )
            BarChart(
                values = weeks.map { it.distanceKm.toFloat() },
                labels = weeks.map { shortDate(it.weekStart) },
                color = ZoneColors.base,
            )
            weeks.takeLast(4).forEach { week ->
                TrendWeekRow(week)
            }
        }
    }
}

@Composable
private fun PaceTrendCard(points: List<PaceTrendPoint>) {
    RcCard(
        modifier = Modifier.padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm),
    ) {
        if (points.isEmpty()) {
            EmptyState(text = stringResource(R.string.trends_empty_pace))
            return@RcCard
        }

        val pacePoints = points.filter { it.avgPaceSeconds != null }
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
            if (pacePoints.isNotEmpty()) {
                LineChart(
                    values = pacePoints.map { it.avgPaceSeconds!!.toFloat() },
                    color = ZoneColors.threshold,
                )
            }
            points.takeLast(5).reversed().forEach { point ->
                PacePointRow(point)
            }
        }
    }
}

@Composable
private fun BarChart(values: List<Float>, labels: List<String>, color: Color) {
    val maxValue = values.maxOrNull()?.coerceAtLeast(1f) ?: 1f
    Row(
        modifier = Modifier.fillMaxWidth().height(150.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        values.forEachIndexed { index, value ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
            ) {
                Box(
                    modifier = Modifier
                        .height((112 * (value / maxValue)).coerceAtLeast(4f).dp)
                        .width(12.dp)
                        .background(color.copy(alpha = 0.72f)),
                )
                Text(labels.getOrElse(index) { "" }, style = RcTypography.labelSmall, color = rcColors.textMuted)
            }
        }
    }
}

@Composable
private fun LineChart(values: List<Float>, color: Color) {
    val minValue = values.minOrNull() ?: 0f
    val maxValue = values.maxOrNull() ?: 1f
    val range = (maxValue - minValue).coerceAtLeast(1f)
    val gridColor = rcColors.borderStrong

    Canvas(modifier = Modifier.fillMaxWidth().height(150.dp)) {
        drawLine(
            color = gridColor,
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            strokeWidth = 1.dp.toPx(),
        )

        val points = values.mapIndexed { index, value ->
            val x = if (values.size == 1) size.width else size.width * index / (values.lastIndex)
            val y = size.height - ((value - minValue) / range) * size.height
            Offset(x, y)
        }

        points.zipWithNext().forEach { (start, end) ->
            drawLine(
                color = color,
                start = start,
                end = end,
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
        points.forEach { point ->
            drawCircle(color = color, radius = 4.dp.toPx(), center = point)
        }
    }
}

@Composable
private fun TrendWeekRow(week: TrendWeek) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(shortDate(week.weekStart), style = RcTypography.monoBody, color = rcColors.textDim, modifier = Modifier.width(56.dp))
        Text("%.1fkm".format(week.distanceKm), style = RcTypography.bodyMedium, color = rcColors.text, modifier = Modifier.weight(1f))
        Text("${week.runCount}회", style = RcTypography.bodySmall, color = rcColors.textDim)
        Text(week.acwr?.let { "ACWR %.2f".format(it) } ?: "--", style = RcTypography.bodySmall, color = rcColors.textMuted)
    }
}

@Composable
private fun PacePointRow(point: PaceTrendPoint) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(point.title, style = RcTypography.bodyMedium, color = rcColors.text)
            Text(point.activityDate, style = RcTypography.bodySmall, color = rcColors.textMuted)
        }
        Text(
            point.avgPaceSeconds?.let { formatPace(it) } ?: point.avgPace ?: "--",
            style = RcTypography.monoBody,
            color = rcColors.textDim,
        )
        Text(point.distanceKm?.let { "%.1fkm".format(it) } ?: "--", style = RcTypography.bodySmall, color = rcColors.textMuted)
    }
}

@Composable
private fun MetricBlock(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueStyle: TextStyle = RcTypography.metricMedium,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Text(label.uppercase(), style = RcTypography.labelSmall, color = rcColors.textMuted)
        Text(value, style = valueStyle, color = rcColors.text)
    }
}

@Composable
private fun EmptyState(text: String) {
    Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
        Text(text, style = RcTypography.bodyMedium, color = rcColors.textDim)
    }
}

private fun shortDate(date: String): String {
    val parts = date.split("-")
    return if (parts.size == 3) "${parts[1]}/${parts[2]}" else date
}
