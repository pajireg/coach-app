package com.suminchoi.coachapp.features.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.suminchoi.coachapp.R
import com.suminchoi.coachapp.core.model.Activity
import com.suminchoi.coachapp.core.model.PlannedWorkout
import com.suminchoi.coachapp.core.model.formatPace
import com.suminchoi.coachapp.core.model.label
import com.suminchoi.coachapp.core.model.toZone
import com.suminchoi.coachapp.design.RcTypography
import com.suminchoi.coachapp.design.Spacing
import com.suminchoi.coachapp.design.components.RcButton
import com.suminchoi.coachapp.design.components.RcCard
import com.suminchoi.coachapp.design.components.RcZoneBadge
import com.suminchoi.coachapp.design.components.zoneColor
import com.suminchoi.coachapp.design.rcColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSheet(
    workout: PlannedWorkout?,
    activity: Activity?,
    onDismiss: () -> Unit,
) {
    if (workout == null) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        val zone = (workout.sessionType ?: "rest").toZone()
        val accent = zoneColor(zone)
        val isCompleted = activity != null

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.screenHorizontal)
                .padding(bottom = 48.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    RcZoneBadge(sessionType = workout.sessionType ?: "rest")
                    StatusPill(
                        text = stringResource(
                            if (isCompleted) R.string.workout_status_completed
                            else if (workout.isRest) R.string.workout_status_rest
                            else R.string.workout_status_planned
                        ),
                        color = if (isCompleted) accent else rcColors.textMuted,
                    )
                }

                Text(workout.name, style = RcTypography.titleLarge, color = rcColors.text)
                Text(
                    text = listOfNotNull(
                        workout.date.ifBlank { null },
                        workout.workoutType,
                        zone.label(),
                    ).joinToString(" · "),
                    style = RcTypography.bodySmall,
                    color = rcColors.textDim,
                )
            }

            RcCard {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    Text(
                        stringResource(R.string.workout_plan_header),
                        style = RcTypography.labelSmall,
                        color = rcColors.textMuted,
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xl),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md),
                    ) {
                        MetricColumn(
                            label = stringResource(R.string.workout_planned_min),
                            value = workout.plannedMinutes?.let { "${it}분" } ?: "-",
                        )
                        MetricColumn(
                            label = stringResource(R.string.workout_type),
                            value = workout.workoutType ?: stringResource(R.string.workout_rest),
                        )
                        MetricColumn(
                            label = stringResource(R.string.workout_intensity),
                            value = zone.label(),
                        )
                    }
                }
            }

            GuidanceCard(
                workout = workout,
                accent = accent,
            )

            if (activity != null) {
                Text(
                    stringResource(R.string.workout_actual_header),
                    style = RcTypography.labelSmall,
                    color = rcColors.textMuted,
                )

                RcCard {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        Text(activity.title, style = RcTypography.titleMedium, color = rcColors.text)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xl),
                            verticalArrangement = Arrangement.spacedBy(Spacing.md),
                        ) {
                            if (activity.distanceKm != null) {
                                MetricColumn(label = stringResource(R.string.activity_distance), value = "%.1fkm".format(activity.distanceKm))
                            }
                            if (activity.avgPaceSeconds != null) {
                                MetricColumn(label = stringResource(R.string.activity_pace), value = formatPace(activity.avgPaceSeconds))
                            } else if (activity.avgPace != null) {
                                MetricColumn(label = stringResource(R.string.activity_pace), value = activity.avgPace)
                            }
                            if (activity.averageHr != null) {
                                MetricColumn(label = stringResource(R.string.activity_hr), value = "${activity.averageHr}bpm")
                            }
                            if (activity.durationSeconds != null) {
                                MetricColumn(label = stringResource(R.string.activity_duration), value = formatDuration(activity.durationSeconds))
                            }
                        }
                        if (activity.executionQuality != null || activity.targetMatchScore != null) {
                            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xl)) {
                                activity.executionQuality?.let {
                                    MetricColumn(label = stringResource(R.string.workout_quality), value = it)
                                }
                                activity.targetMatchScore?.let {
                                    MetricColumn(label = stringResource(R.string.workout_match_score), value = "${(it * 100).toInt()}%")
                                }
                            }
                        }
                    }
                }
            }

            RcButton(
                text = stringResource(
                    if (workout.isRest) R.string.workout_close_cta
                    else R.string.workout_garmin_cta
                ),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun MetricColumn(label: String, value: String) {
    Column {
        Text(label.uppercase(), style = RcTypography.labelSmall, color = rcColors.textMuted)
        Text(value, style = RcTypography.monoBody, color = rcColors.text)
    }
}

@Composable
private fun StatusPill(text: String, color: Color) {
    Text(
        text = text,
        style = RcTypography.labelSmall,
        color = color,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
    )
}

@Composable
private fun GuidanceCard(workout: PlannedWorkout, accent: Color) {
    val steps = guidanceFor(workout)

    RcCard {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
            Text(
                stringResource(R.string.workout_guidance_header),
                style = RcTypography.labelSmall,
                color = rcColors.textMuted,
            )
            steps.forEachIndexed { index, step ->
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (index == 0) accent else rcColors.textMuted.copy(alpha = 0.5f)),
                    )
                    Text(step, style = RcTypography.bodyMedium, color = rcColors.textDim)
                }
            }
        }
    }
}

private fun guidanceFor(workout: PlannedWorkout): List<String> {
    if (workout.isRest) {
        return listOf(
            "가벼운 산책이나 스트레칭 정도만 진행하세요.",
            "수면, 수분, 통증 상태를 우선 확인하세요.",
        )
    }

    return when ((workout.sessionType ?: "").lowercase()) {
        "recovery" -> listOf(
            "대화가 편한 강도로 시작부터 끝까지 유지하세요.",
            "다리가 무겁다면 시간을 줄이고 회복을 우선하세요.",
        )
        "base" -> listOf(
            "초반 10분은 여유 있게 올리고 이후 일정한 호흡을 유지하세요.",
            "마지막까지 힘을 남기는 페이스가 목표입니다.",
        )
        "threshold" -> listOf(
            "워밍업 후 약간 버거운 강도를 안정적으로 유지하세요.",
            "호흡이 무너지면 즉시 페이스를 낮추세요.",
        )
        "interval" -> listOf(
            "빠른 구간과 회복 구간을 명확히 나누세요.",
            "첫 반복을 과하게 빠르게 시작하지 않는 것이 중요합니다.",
        )
        "long" -> listOf(
            "초반은 의도적으로 느리게 시작하고 보급 타이밍을 지키세요.",
            "후반 20분에도 자세가 유지되는 강도를 선택하세요.",
        )
        else -> listOf(
            "오늘 컨디션에 맞춰 무리 없는 강도로 진행하세요.",
            "통증이 있으면 훈련을 중단하고 피드백을 남기세요.",
        )
    }
}

private fun formatDuration(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return if (hours > 0) "${hours}시간 ${minutes}분" else "${minutes}분"
}
