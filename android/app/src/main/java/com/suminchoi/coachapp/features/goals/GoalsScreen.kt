package com.suminchoi.coachapp.features.goals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.suminchoi.coachapp.design.components.RcScreen
import com.suminchoi.coachapp.design.rcColors

@Composable
fun GoalsScreen(viewModel: GoalsViewModel = hiltViewModel()) {
    val form by viewModel.form.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    LaunchedEffect(form.submitted) {
        if (form.submitted) {
            snackbarHost.showSnackbar("목표가 저장되었습니다.")
            viewModel.dismissSuccess()
        }
    }

    RcScreen(title = stringResource(R.string.tab_goals)) {
        item {
            RcCard(modifier = Modifier.padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm)) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    Text(
                        stringResource(R.string.goals_race_goal_title),
                        style = RcTypography.titleMedium,
                        color = rcColors.text,
                    )

                    OutlinedTextField(
                        value = form.targetDistanceKm,
                        onValueChange = viewModel::updateDistance,
                        label = { Text(stringResource(R.string.goals_distance_label)) },
                        placeholder = { Text("42.195") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )

                    OutlinedTextField(
                        value = form.targetDate,
                        onValueChange = viewModel::updateDate,
                        label = { Text(stringResource(R.string.goals_date_label)) },
                        placeholder = { Text("2025-10-01") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )

                    OutlinedTextField(
                        value = form.notes,
                        onValueChange = viewModel::updateNotes,
                        label = { Text(stringResource(R.string.goals_notes_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                    )

                    if (form.error != null) {
                        Text(form.error!!, style = RcTypography.bodySmall, color = ZoneColors.interval)
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
