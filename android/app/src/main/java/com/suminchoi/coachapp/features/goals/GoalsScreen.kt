package com.suminchoi.coachapp.features.goals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.suminchoi.coachapp.R
import com.suminchoi.coachapp.design.RcTypography
import com.suminchoi.coachapp.design.Spacing
import com.suminchoi.coachapp.design.ZoneColors
import com.suminchoi.coachapp.design.components.RcButton
import com.suminchoi.coachapp.design.components.RcCard
import com.suminchoi.coachapp.design.components.RcTextField
import com.suminchoi.coachapp.design.rcColors

@Composable
fun GoalsScreen(viewModel: GoalsViewModel = hiltViewModel()) {
    val form by viewModel.form.collectAsStateWithLifecycle()

    com.suminchoi.coachapp.design.components.RcScreen(title = stringResource(R.string.tab_goals)) {
        item {
            RcCard(modifier = Modifier.padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm)) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    Text(
                        stringResource(R.string.goals_race_goal_title),
                        style = RcTypography.titleMedium,
                        color = rcColors.text,
                    )

                    RcTextField(
                        value = form.goalName,
                        onValueChange = viewModel::updateGoalName,
                        label = stringResource(R.string.goals_name_label),
                        placeholder = "서울 10K PB",
                        modifier = Modifier.fillMaxWidth(),
                    )

                    RcTextField(
                        value = form.distance,
                        onValueChange = viewModel::updateDistance,
                        label = stringResource(R.string.goals_distance_label),
                        placeholder = "10K",
                        modifier = Modifier.fillMaxWidth(),
                    )

                    RcTextField(
                        value = form.raceDate,
                        onValueChange = viewModel::updateDate,
                        label = stringResource(R.string.goals_date_label),
                        placeholder = "2026-10-01",
                        modifier = Modifier.fillMaxWidth(),
                    )

                    RcTextField(
                        value = form.goalTime,
                        onValueChange = viewModel::updateGoalTime,
                        label = stringResource(R.string.goals_time_label),
                        placeholder = "49:00",
                        modifier = Modifier.fillMaxWidth(),
                    )

                    RcTextField(
                        value = form.targetPace,
                        onValueChange = viewModel::updateTargetPace,
                        label = stringResource(R.string.goals_pace_label),
                        placeholder = "4:54",
                        modifier = Modifier.fillMaxWidth(),
                    )

                    if (form.error != null) {
                        Text(form.error!!, style = RcTypography.bodySmall, color = ZoneColors.interval)
                    }
                    if (form.submitted) {
                        Text(stringResource(R.string.goals_saved), style = RcTypography.bodySmall, color = ZoneColors.base)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    RcButton(
                        text = stringResource(R.string.goals_save_cta),
                        onClick = viewModel::submit,
                        isLoading = form.isLoading,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
