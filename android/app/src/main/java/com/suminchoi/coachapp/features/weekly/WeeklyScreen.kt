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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.suminchoi.coachapp.R
import com.suminchoi.coachapp.core.model.PlannedWorkout
import com.suminchoi.coachapp.core.model.toZone
import com.suminchoi.coachapp.design.RcTypography
import com.suminchoi.coachapp.design.Spacing
import com.suminchoi.coachapp.design.components.RcCard
import com.suminchoi.coachapp.design.components.RcScreen
import com.suminchoi.coachapp.design.components.RcZoneDot
import com.suminchoi.coachapp.design.components.zoneColor
import com.suminchoi.coachapp.design.rcColors

@Composable
fun WeeklyScreen(
    onOpenWorkout: (String) -> Unit,
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
                val grouped = workouts.groupBy { it.date }

                item {
                    WeekStrip(
                        workouts = workouts,
                        onTap = { onOpenWorkout(it.id) },
                    )
                }

                grouped.forEach { (date, dayWorkouts) ->
                    item {
                        Text(
                            text = date,
                            style = RcTypography.labelSmall,
                            color = rcColors.textMuted,
                            modifier = Modifier.padding(
                                horizontal = Spacing.screenHorizontal,
                                vertical = Spacing.sm,
                            ),
                        )
                    }
                    dayWorkouts.forEach { workout ->
                        item {
                            RcCard(
                                modifier = Modifier
                                    .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.xs)
                                    .clickable { onOpenWorkout(workout.id) },
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                                ) {
                                    RcZoneDot(sessionType = workout.sessionType)
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(workout.name, style = RcTypography.bodyMedium, color = rcColors.text)
                                        Text(
                                            "${workout.plannedMinutes}분",
                                            style = RcTypography.bodySmall,
                                            color = rcColors.textDim,
                                        )
                                    }
                                }
                            }
                        }
                    }
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
                }
            }
        }
    }
}

@Composable
private fun WeekStrip(workouts: List<PlannedWorkout>, onTap: (PlannedWorkout) -> Unit) {
    val days = listOf("월", "화", "수", "목", "금", "토", "일")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        workouts.take(7).forEachIndexed { index, workout ->
            val color = zoneColor(workout.sessionType.toZone())
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onTap(workout) }
                    .background(color.copy(alpha = 0.12f))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .width(52.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(days.getOrElse(index) { "" }, style = RcTypography.labelSmall, color = rcColors.textMuted)
                Text(
                    workout.date.takeLast(2).trimStart('0'),
                    style = RcTypography.titleMedium,
                    color = rcColors.text,
                    textAlign = TextAlign.Center,
                )
                if (!workout.isRest) {
                    Box(
                        modifier = Modifier.size(6.dp).clip(CircleShape).background(color),
                    )
                    Text("${workout.plannedMinutes}'", style = RcTypography.bodySmall, color = color)
                } else {
                    Text(stringResource(R.string.workout_rest), style = RcTypography.bodySmall, color = rcColors.textMuted)
                }
            }
        }
    }
}
