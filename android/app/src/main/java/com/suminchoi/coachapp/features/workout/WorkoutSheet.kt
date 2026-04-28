package com.suminchoi.coachapp.features.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.suminchoi.coachapp.R
import com.suminchoi.coachapp.core.model.Activity
import com.suminchoi.coachapp.core.model.PlannedWorkout
import com.suminchoi.coachapp.core.model.formatPace
import com.suminchoi.coachapp.design.RcTypography
import com.suminchoi.coachapp.design.Spacing
import com.suminchoi.coachapp.design.ZoneColors
import com.suminchoi.coachapp.design.components.RcButton
import com.suminchoi.coachapp.design.components.RcZoneBadge
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.screenHorizontal)
                .padding(bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            RcZoneBadge(sessionType = workout.sessionType ?: "rest")

            Text(workout.name, style = RcTypography.titleLarge, color = rcColors.text)

            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xl)) {
                MetricColumn(
                    label = stringResource(R.string.workout_planned_min),
                    value = workout.plannedMinutes?.let { "${it}분" } ?: "-",
                )
                if (workout.workoutType != null) {
                    MetricColumn(
                        label = stringResource(R.string.workout_type),
                        value = workout.workoutType,
                    )
                }
            }

            if (activity != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    stringResource(R.string.workout_actual_header),
                    style = RcTypography.labelSmall,
                    color = rcColors.textMuted,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xl)) {
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
                }
                if (activity.executionQuality != null) {
                    MetricColumn(label = stringResource(R.string.workout_quality), value = activity.executionQuality)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            RcButton(
                text = stringResource(R.string.workout_garmin_cta),
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
