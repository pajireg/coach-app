package com.suminchoi.coachapp.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.suminchoi.coachapp.R
import com.suminchoi.coachapp.core.model.Activity
import com.suminchoi.coachapp.core.model.DashboardResponse
import com.suminchoi.coachapp.core.model.PlannedWorkout
import com.suminchoi.coachapp.core.model.Zone
import com.suminchoi.coachapp.core.model.formatPace
import com.suminchoi.coachapp.core.model.label
import com.suminchoi.coachapp.core.model.toZone
import com.suminchoi.coachapp.design.RcTypography
import com.suminchoi.coachapp.design.Spacing
import com.suminchoi.coachapp.design.ZoneColors
import com.suminchoi.coachapp.design.components.RcCard
import com.suminchoi.coachapp.design.components.RcScreen
import com.suminchoi.coachapp.design.components.RcSectionHeader
import com.suminchoi.coachapp.design.components.RcZoneBadge
import com.suminchoi.coachapp.design.components.RcZoneDot
import com.suminchoi.coachapp.design.components.zoneColor
import com.suminchoi.coachapp.design.rcColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenFeedback: () -> Unit,
    onOpenWorkout: (PlannedWorkout, Activity?) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = state is HomeUiState.Loading,
            onRefresh = viewModel::load,
            modifier = Modifier.fillMaxSize(),
        ) {
            RcScreen(
                title = stringResource(R.string.tab_home),
                eyebrow = stringResource(R.string.home_eyebrow),
            ) {
                when (val s = state) {
                    is HomeUiState.Loading -> item {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    is HomeUiState.Error -> item {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            Text(s.message, style = RcTypography.bodyMedium, color = rcColors.textDim)
                        }
                    }

                    is HomeUiState.Success -> {
                        val dashboard = s.dashboard
                        val today = dashboard.currentPlan.firstOrNull()

                        item {
                            if (today != null) {
                                HeroCard(
                                    workout = today,
                                    onClick = { onOpenWorkout(today, dashboard.matchActivity(today)) },
                                )
                            }
                        }

                        if (dashboard.recentActivities.isNotEmpty()) {
                            item {
                                RcSectionHeader(title = stringResource(R.string.home_recent_activity))
                            }
                            item {
                                ActivityCard(activity = dashboard.recentActivities.first())
                            }
                        }

                        if (dashboard.currentPlan.size > 1) {
                            item {
                                RcSectionHeader(title = stringResource(R.string.home_plan_preview))
                            }
                            items(dashboard.currentPlan.drop(1).take(3)) { workout ->
                                WorkoutRow(
                                    workout = workout,
                                    onClick = { onOpenWorkout(workout, dashboard.matchActivity(workout)) },
                                )
                            }
                        }
                    }
                }
            }
        }

        // FAB — chat/feedback icon, dark pill above tab bar
        val fabBg = rcColors.text
        val fabFg = rcColors.bg
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 132.dp)
                .size(52.dp)
                .clip(CircleShape)
                .background(fabBg)
                .clickable { onOpenFeedback() },
            contentAlignment = Alignment.Center,
        ) {
            androidx.compose.foundation.Canvas(modifier = Modifier.size(20.dp)) {
                val s = size.width
                val color = fabFg
                val stroke = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = 1.8.dp.toPx(),
                    join = androidx.compose.ui.graphics.StrokeJoin.Round,
                )
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(s * 0.2f, s * 0.25f)
                    lineTo(s * 0.8f, s * 0.25f)
                    lineTo(s * 0.8f, s * 0.65f)
                    lineTo(s * 0.4f, s * 0.65f)
                    lineTo(s * 0.15f, s * 0.90f)
                    lineTo(s * 0.2f, s * 0.65f)
                    close()
                }
                drawPath(path, color, style = stroke)
                // + icon inside bubble
                drawLine(color, androidx.compose.ui.geometry.Offset(s * 0.5f, s * 0.35f), androidx.compose.ui.geometry.Offset(s * 0.5f, s * 0.55f), 1.8.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                drawLine(color, androidx.compose.ui.geometry.Offset(s * 0.40f, s * 0.45f), androidx.compose.ui.geometry.Offset(s * 0.60f, s * 0.45f), 1.8.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
            }
        }
    }
}

@Composable
private fun HeroCard(workout: PlannedWorkout, onClick: () -> Unit) {
    val sessionType = workout.sessionType ?: "rest"
    val zone = sessionType.toZone()
    val accent = zoneColor(zone)

    Box(
        modifier = Modifier
            .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() },
    ) {
        // card background via RcCard-like treatment
        val colors = rcColors
        val bgColor = if (colors.isDark) Color(0xFF1C263C).copy(alpha = 0.62f)
        else Color.White.copy(alpha = 0.72f)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind { drawRect(bgColor) }
                .padding(top = 3.dp),
        ) {
            // top accent stripe
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(accent)
                    .align(Alignment.TopCenter),
            )

            Column(
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RcZoneBadge(sessionType = sessionType)
                    if (workout.date.isNotBlank()) {
                        Text(
                            text = workout.date,
                            style = RcTypography.bodySmall,
                            color = rcColors.textMuted,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = workout.name,
                    style = RcTypography.titleLarge,
                    color = rcColors.text,
                )
                if (workout.workoutType != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = workout.workoutType,
                        style = RcTypography.bodySmall,
                        color = rcColors.textDim,
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                // metric row with divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    if (workout.plannedMinutes != null) {
                        HeroMetric(
                            label = stringResource(R.string.workout_planned_min),
                            value = "${workout.plannedMinutes}",
                            unit = "분",
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (workout.plannedMinutes != null && !workout.isRest) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .width(0.5.dp)
                                .height(48.dp)
                                .background(rcColors.border),
                        )
                    }
                    if (!workout.isRest) {
                        HeroMetric(
                            label = stringResource(R.string.workout_intensity),
                            value = zone.label(),
                            unit = "",
                            modifier = Modifier.weight(1.1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroMetric(label: String, value: String, unit: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = label.uppercase(),
            style = RcTypography.labelSmall,
            color = rcColors.textMuted,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                style = RcTypography.metricMedium,
                color = rcColors.text,
            )
            if (unit.isNotEmpty()) {
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = unit,
                    style = RcTypography.bodySmall,
                    color = rcColors.textDim,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun ActivityCard(activity: Activity) {
    RcCard(modifier = Modifier.padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.xs)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            // zone icon box
            val accent = zoneColor((activity.sessionType ?: "base").toZone())
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.20f)),
                contentAlignment = Alignment.Center,
            ) {
                // checkmark
                androidx.compose.foundation.Canvas(modifier = Modifier.size(20.dp)) {
                    val s = size.width
                    val stroke = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 2.2.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round,
                    )
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(s * 0.2f, s * 0.5f)
                        lineTo(s * 0.44f, s * 0.74f)
                        lineTo(s * 0.8f, s * 0.26f)
                    }
                    drawPath(path, accent, style = stroke)
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(activity.title, style = RcTypography.titleMedium, color = rcColors.text)
                    if (activity.targetMatchScore != null) {
                        Text(
                            text = "${(activity.targetMatchScore * 100).toInt()}%",
                            style = RcTypography.monoBody,
                            color = ZoneColors.base,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    if (activity.distanceKm != null) {
                        Text("%.1fkm".format(activity.distanceKm), style = RcTypography.monoBody, color = rcColors.textDim)
                    }
                    if (activity.avgPaceSeconds != null) {
                        Text(formatPace(activity.avgPaceSeconds), style = RcTypography.monoBody, color = rcColors.textDim)
                    } else if (activity.avgPace != null) {
                        Text(activity.avgPace, style = RcTypography.monoBody, color = rcColors.textDim)
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkoutRow(workout: PlannedWorkout, onClick: () -> Unit) {
    val accent = zoneColor(workout.sessionType?.toZone() ?: com.suminchoi.coachapp.core.model.Zone.REST)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = Spacing.screenHorizontal, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        // date column
        Column(
            modifier = Modifier
                .width(40.dp)
                .padding(end = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = workout.date.takeLast(2).trimStart('0'),
                style = RcTypography.monoBody,
                color = rcColors.text,
            )
            Text(
                text = workout.date.take(7).takeLast(2) + "월",
                style = RcTypography.labelSmall,
                color = rcColors.textMuted,
            )
        }
        // zone bar
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(32.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(accent),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(workout.name, style = RcTypography.bodyMedium, color = rcColors.text)
            if (workout.workoutType != null) {
                Text(workout.workoutType, style = RcTypography.bodySmall, color = rcColors.textDim)
            }
        }
        if (!workout.isRest && workout.plannedMinutes != null) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${workout.plannedMinutes}",
                    style = RcTypography.monoBody,
                    color = rcColors.text,
                )
                Text("MIN", style = RcTypography.labelSmall, color = rcColors.textMuted)
            }
        }
    }
    Box(
        modifier = Modifier
            .padding(start = Spacing.screenHorizontal)
            .fillMaxWidth()
            .height(0.5.dp)
            .background(rcColors.border),
    )
}

private fun DashboardResponse.matchActivity(workout: PlannedWorkout): Activity? =
    recentActivities.firstOrNull { it.activityDate == workout.date }
