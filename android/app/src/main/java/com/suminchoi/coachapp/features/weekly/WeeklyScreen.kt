package com.suminchoi.coachapp.features.weekly

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.suminchoi.coachapp.R
import com.suminchoi.coachapp.core.model.Activity
import com.suminchoi.coachapp.core.model.DashboardResponse
import com.suminchoi.coachapp.core.model.PlannedWorkout
import com.suminchoi.coachapp.core.model.Zone
import com.suminchoi.coachapp.core.model.label
import com.suminchoi.coachapp.core.model.toZone
import com.suminchoi.coachapp.design.RcTypography
import com.suminchoi.coachapp.design.Spacing
import com.suminchoi.coachapp.design.components.RcScreen
import com.suminchoi.coachapp.design.components.zoneColor
import com.suminchoi.coachapp.design.rcColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WeeklyScreen(
    onOpenWorkout: (PlannedWorkout, Activity?) -> Unit,
    viewModel: WeeklyViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    RcScreen(title = stringResource(R.string.tab_weekly)) {
        when (val s = state) {
            is WeeklyUiState.Loading -> item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is WeeklyUiState.Error -> item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text(s.message, style = RcTypography.bodyMedium, color = rcColors.textDim)
                }
            }

            is WeeklyUiState.Success -> {
                val workouts = s.dashboard.currentPlan
                val today = try { LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) } catch (_: Exception) { "" }

                item {
                    WeekStrip(
                        workouts = workouts,
                        today = today,
                        onTap = { onOpenWorkout(it, s.dashboard.matchActivity(it)) },
                    )
                }

                // top divider before list
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(rcColors.border))
                }

                if (workouts.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            Text(
                                stringResource(R.string.weekly_empty),
                                style = RcTypography.bodyMedium,
                                color = rcColors.textDim,
                            )
                        }
                    }
                } else {
                    workouts.forEach { workout ->
                        item {
                            WeeklyListRow(
                                workout = workout,
                                isToday = workout.date == today,
                                isDone = s.dashboard.matchActivity(workout) != null,
                                onClick = { onOpenWorkout(workout, s.dashboard.matchActivity(workout)) },
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun DashboardResponse.matchActivity(workout: PlannedWorkout): Activity? =
    recentActivities.firstOrNull { it.activityDate == workout.date }

@Composable
private fun WeekStrip(
    workouts: List<PlannedWorkout>,
    today: String,
    onTap: (PlannedWorkout) -> Unit,
) {
    val dayLabels = listOf("월", "화", "수", "목", "금", "토", "일")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = Spacing.screenHorizontal - 4.dp, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        workouts.take(7).forEachIndexed { index, workout ->
            val zone = (workout.sessionType ?: "rest").toZone()
            val accent = zoneColor(zone)
            val isToday = workout.date == today
            val isRest = workout.isRest

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onTap(workout) }
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    dayLabels.getOrElse(index) { "" },
                    style = RcTypography.labelSmall,
                    color = if (isToday) rcColors.text else rcColors.textMuted,
                )
                Box(
                    modifier = Modifier.size(38.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    // cell background
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                when {
                                    isToday -> accent
                                    isRest -> rcColors.bgElev2
                                    else -> accent.copy(alpha = 0.22f)
                                }
                            ),
                    )
                    Text(
                        text = workout.date.takeLast(2).trimStart('0'),
                        style = RcTypography.titleMedium,
                        color = when {
                            isToday -> Color(0xFF0B1220)
                            isRest -> rcColors.textDim
                            else -> accent
                        },
                        textAlign = TextAlign.Center,
                    )
                }
                // zone dot
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = if (isRest) 0.3f else 1f)),
                )
            }
        }
    }
}

@Composable
private fun WeeklyListRow(
    workout: PlannedWorkout,
    isToday: Boolean,
    isDone: Boolean,
    onClick: () -> Unit,
) {
    val zone = (workout.sessionType ?: "rest").toZone()
    val accent = zoneColor(zone)
    val muted = workout.isRest

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // date column
            Column(
                modifier = Modifier
                    .width(56.dp)
                    .padding(start = Spacing.screenHorizontal, end = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = workout.date.takeLast(2).trimStart('0'),
                    style = RcTypography.monoBody,
                    color = if (isToday) rcColors.text else rcColors.textDim,
                )
                val dow = try {
                    val d = LocalDate.parse(workout.date)
                    listOf("MON","TUE","WED","THU","FRI","SAT","SUN")[d.dayOfWeek.value - 1]
                } catch (_: Exception) { "" }
                Text(
                    text = dow,
                    style = RcTypography.labelSmall,
                    color = if (isToday) accent else rcColors.textMuted,
                )
            }

            // thin vertical divider
            Box(
                modifier = Modifier
                    .width(0.5.dp)
                    .height(32.dp)
                    .background(rcColors.border),
            )

            Spacer(modifier = Modifier.width(14.dp))

            // zone bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(28.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accent.copy(alpha = if (muted) 0.4f else 1f)),
            )

            Spacer(modifier = Modifier.width(Spacing.sm))

            // name + rationale
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        workout.name,
                        style = RcTypography.bodyMedium,
                        color = rcColors.text.copy(alpha = if (muted) 0.55f else 1f),
                    )
                    if (isToday) {
                        Text(
                            "오늘",
                            style = RcTypography.labelSmall,
                            color = rcColors.bg,
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(rcColors.text)
                                .padding(horizontal = 5.dp, vertical = 2.dp),
                        )
                    }
                    if (isDone) {
                        // small check icon
                        Box(
                            modifier = Modifier.size(14.dp).drawBehind {
                                val s = size.width
                                val stroke = Stroke(1.8.dp.toPx(), cap = StrokeCap.Round)
                                val path = Path().apply {
                                    moveTo(s * 0.15f, s * 0.5f)
                                    lineTo(s * 0.44f, s * 0.78f)
                                    lineTo(s * 0.85f, s * 0.22f)
                                }
                                drawPath(path, zoneColor(Zone.BASE), style = stroke)
                            },
                        )
                    }
                }
                if (workout.workoutType != null) {
                    Text(workout.workoutType, style = RcTypography.bodySmall, color = rcColors.textDim)
                }
            }

            // minutes
            if (!workout.isRest && workout.plannedMinutes != null) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(end = Spacing.screenHorizontal),
                ) {
                    Text(
                        "${workout.plannedMinutes}",
                        style = RcTypography.monoBody,
                        color = rcColors.text,
                    )
                    Text("MIN", style = RcTypography.labelSmall, color = rcColors.textMuted)
                }
            } else {
                Spacer(modifier = Modifier.width(Spacing.screenHorizontal))
                Text(
                    "—",
                    style = RcTypography.bodySmall,
                    color = rcColors.textMuted,
                    modifier = Modifier.padding(end = Spacing.screenHorizontal),
                )
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
}
