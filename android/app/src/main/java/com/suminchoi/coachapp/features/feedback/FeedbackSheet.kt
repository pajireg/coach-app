package com.suminchoi.coachapp.features.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.suminchoi.coachapp.design.components.ScoreSlider
import com.suminchoi.coachapp.design.rcColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackSheet(
    onDismiss: () -> Unit,
    viewModel: FeedbackViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.screenHorizontal)
                .padding(bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text(
                stringResource(R.string.feedback_title),
                style = RcTypography.titleLarge,
                color = rcColors.text,
            )

            ScoreSlider(
                label = stringResource(R.string.feedback_fatigue),
                value = state.fatigue,
                onValueChange = { viewModel.update { copy(fatigue = it) } },
            )
            ScoreSlider(
                label = stringResource(R.string.feedback_soreness),
                value = state.soreness,
                onValueChange = { viewModel.update { copy(soreness = it) } },
            )
            ScoreSlider(
                label = stringResource(R.string.feedback_stress),
                value = state.stress,
                onValueChange = { viewModel.update { copy(stress = it) } },
            )
            ScoreSlider(
                label = stringResource(R.string.feedback_motivation),
                value = state.motivation,
                onValueChange = { viewModel.update { copy(motivation = it) } },
            )
            ScoreSlider(
                label = stringResource(R.string.feedback_sleep),
                value = state.sleep,
                onValueChange = { viewModel.update { copy(sleep = it) } },
            )

            OutlinedTextField(
                value = state.painNotes,
                onValueChange = { viewModel.update { copy(painNotes = it) } },
                label = { Text(stringResource(R.string.feedback_pain_notes)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
            )
            OutlinedTextField(
                value = state.notes,
                onValueChange = { viewModel.update { copy(notes = it) } },
                label = { Text(stringResource(R.string.feedback_notes)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
            )

            if (state.error != null) {
                Text(state.error!!, style = RcTypography.bodySmall, color = ZoneColors.interval)
            }

            Spacer(modifier = Modifier.height(4.dp))

            RcButton(
                text = stringResource(R.string.feedback_submit_cta),
                onClick = { viewModel.submit(onDismiss) },
                isLoading = state.isLoading,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
