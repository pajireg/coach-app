package com.suminchoi.coachapp.features.home

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.suminchoi.coachapp.R
import com.suminchoi.coachapp.core.model.Activity
import com.suminchoi.coachapp.core.model.PlannedWorkout
import com.suminchoi.coachapp.core.model.formatPace
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
    onOpenWorkout: (PlannedWorkout) -> Unit,
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
                                HeroCard(workout = today, onClick = { onOpenWorkout(today) })
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
                                    onClick = { onOpenWorkout(workout) },
                                )
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onOpenFeedback,
            shape = CircleShape,
            containerColor = ZoneColors.base,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 132.dp),
        ) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.home_feedback_fab), tint = androidx.compose.ui.graphics.Color.White)
        }
    }
}

@Composable
private fun HeroCard(workout: PlannedWorkout, onClick: () -> Unit) {
    val sessionType = workout.sessionType ?: "rest"
    RcCard(
        modifier = Modifier
            .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm)
            .fillMaxWidth(),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            RcZoneBadge(sessionType = sessionType)
            Text(workout.name, style = RcTypography.titleLarge, color = rcColors.text)
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                MetricChip(
                    label = stringResource(R.string.workout_planned_min),
                    value = workout.plannedMinutes?.let { "${it}분" } ?: "-",
                )
                if (workout.workoutType != null) {
                    MetricChip(label = stringResource(R.string.workout_type), value = workout.workoutType)
                }
            }
        }
    }
}

@Composable
private fun ActivityCard(activity: Activity) {
    RcCard(modifier = Modifier.padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm)) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RcZoneDot(sessionType = activity.sessionType ?: "base")
                Spacer(modifier = Modifier.width(Spacing.sm))
                Text(activity.title, style = RcTypography.titleMedium, color = rcColors.text)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                if (activity.distanceKm != null) {
                    MetricChip(label = stringResource(R.string.activity_distance), value = "%.1fkm".format(activity.distanceKm))
                }
                if (activity.avgPaceSeconds != null) {
                    MetricChip(label = stringResource(R.string.activity_pace), value = formatPace(activity.avgPaceSeconds))
                } else if (activity.avgPace != null) {
                    MetricChip(label = stringResource(R.string.activity_pace), value = activity.avgPace)
                }
                if (activity.averageHr != null) {
                    MetricChip(label = stringResource(R.string.activity_hr), value = "${activity.averageHr}bpm")
                }
            }
        }
    }
}

@Composable
private fun WorkoutRow(workout: PlannedWorkout, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        RcZoneDot(sessionType = workout.sessionType ?: "rest")
        Column(modifier = Modifier.weight(1f)) {
            Text(workout.name, style = RcTypography.bodyMedium, color = rcColors.text)
            Text(workout.date, style = RcTypography.bodySmall, color = rcColors.textMuted)
        }
        Text(workout.plannedMinutes?.let { "${it}분" } ?: "-", style = RcTypography.monoBody, color = rcColors.textDim)
    }
}

@Composable
private fun MetricChip(label: String, value: String) {
    Column {
        Text(label.uppercase(), style = RcTypography.labelSmall, color = rcColors.textMuted)
        Text(value, style = RcTypography.monoBody, color = rcColors.text)
    }
}
