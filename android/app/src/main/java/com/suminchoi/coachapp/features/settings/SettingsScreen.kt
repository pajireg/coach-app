package com.suminchoi.coachapp.features.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.suminchoi.coachapp.R
import com.suminchoi.coachapp.core.model.Integration
import com.suminchoi.coachapp.design.RcTypography
import com.suminchoi.coachapp.design.Spacing
import com.suminchoi.coachapp.design.ZoneColors
import com.suminchoi.coachapp.design.components.RcButton
import com.suminchoi.coachapp.design.components.RcButtonStyle
import com.suminchoi.coachapp.design.components.RcCard
import com.suminchoi.coachapp.design.components.RcScreen
import com.suminchoi.coachapp.design.components.RcSectionHeader
import com.suminchoi.coachapp.design.components.RcZoneBadge
import com.suminchoi.coachapp.design.rcColors

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    RcScreen(title = stringResource(R.string.tab_settings)) {
        when (val s = state) {
            is SettingsUiState.Loading -> item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is SettingsUiState.Error -> item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text(s.message, style = RcTypography.bodyMedium, color = rcColors.textDim)
                }
            }

            is SettingsUiState.Success -> {
                val user = s.user

                item { RcSectionHeader(title = stringResource(R.string.settings_profile_section)) }
                item {
                    RcCard(modifier = Modifier.padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.xs)) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(user.displayName?.ifBlank { null } ?: user.email.ifBlank { user.externalKey }, style = RcTypography.titleMedium, color = rcColors.text)
                            if (user.garminEmail != null) {
                                Text(user.garminEmail, style = RcTypography.bodySmall, color = rcColors.textDim)
                            }
                        }
                    }
                }

                item { RcSectionHeader(title = stringResource(R.string.settings_preferences_section)) }
                item {
                    RcCard(modifier = Modifier.padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.xs)) {
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                            PreferenceRow(label = stringResource(R.string.settings_timezone), value = user.preferences.timezone)
                            PreferenceRow(label = stringResource(R.string.settings_locale), value = user.preferences.locale)
                            PreferenceRow(label = stringResource(R.string.settings_run_mode), value = user.preferences.runMode)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(stringResource(R.string.settings_include_strength), style = RcTypography.bodyMedium, color = rcColors.text)
                                Switch(checked = user.preferences.includeStrength, onCheckedChange = viewModel::updateIncludeStrength)
                            }
                        }
                    }
                }

                item { RcSectionHeader(title = stringResource(R.string.settings_availability_section)) }
                item {
                    RcCard(modifier = Modifier.padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.xs)) {
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                            Text(stringResource(R.string.settings_weekday), style = RcTypography.bodyMedium, color = rcColors.text)
                            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                                listOf("월", "화", "수", "목", "금", "토", "일").forEachIndexed { index, label ->
                                    RcButton(
                                        text = label,
                                        onClick = { viewModel.updateAvailabilityWeekday(index) },
                                        style = if (s.availabilityWeekday == index) RcButtonStyle.PRIMARY else RcButtonStyle.SECONDARY,
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                            }
                            OutlinedTextField(
                                value = s.availabilityMaxMinutes,
                                onValueChange = viewModel::updateAvailabilityMaxMinutes,
                                label = { Text(stringResource(R.string.settings_max_minutes)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                            )
                            OutlinedTextField(
                                value = s.availabilitySessionType,
                                onValueChange = viewModel::updateAvailabilitySessionType,
                                label = { Text(stringResource(R.string.settings_session_type)) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                            )
                            RcButton(
                                text = stringResource(R.string.settings_save_availability),
                                onClick = viewModel::saveAvailability,
                                isLoading = s.isAvailabilitySaving,
                                style = RcButtonStyle.SECONDARY,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }

                item { RcSectionHeader(title = stringResource(R.string.settings_injury_section)) }
                item {
                    RcCard(modifier = Modifier.padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.xs)) {
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                            OutlinedTextField(
                                value = s.injuryArea,
                                onValueChange = viewModel::updateInjuryArea,
                                label = { Text(stringResource(R.string.settings_injury_area)) },
                                placeholder = { Text("왼쪽 무릎") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                            )
                            OutlinedTextField(
                                value = s.injurySeverity,
                                onValueChange = viewModel::updateInjurySeverity,
                                label = { Text(stringResource(R.string.settings_injury_severity)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                            )
                            OutlinedTextField(
                                value = s.injuryNotes,
                                onValueChange = viewModel::updateInjuryNotes,
                                label = { Text(stringResource(R.string.settings_injury_notes)) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                            )
                            RcButton(
                                text = stringResource(R.string.settings_save_injury),
                                onClick = viewModel::saveInjury,
                                isLoading = s.isInjurySaving,
                                style = RcButtonStyle.SECONDARY,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }

                item { RcSectionHeader(title = stringResource(R.string.settings_integrations_section)) }
                item {
                    RcCard(modifier = Modifier.padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.xs)) {
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                            Text(stringResource(R.string.settings_garmin_connect), style = RcTypography.titleMedium, color = rcColors.text)
                            OutlinedTextField(
                                value = s.garminEmail,
                                onValueChange = viewModel::updateGarminEmail,
                                label = { Text(stringResource(R.string.settings_garmin_email)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                            )
                            OutlinedTextField(
                                value = s.garminPassword,
                                onValueChange = viewModel::updateGarminPassword,
                                label = { Text(stringResource(R.string.settings_garmin_password)) },
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                                RcButton(
                                    text = stringResource(R.string.settings_garmin_save),
                                    onClick = viewModel::connectGarmin,
                                    isLoading = s.isGarminSaving,
                                    style = RcButtonStyle.SECONDARY,
                                    modifier = Modifier.weight(1f),
                                )
                                RcButton(
                                    text = stringResource(R.string.settings_garmin_disconnect),
                                    onClick = viewModel::disconnectGarmin,
                                    isLoading = s.isGarminDisconnecting,
                                    style = RcButtonStyle.GHOST,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }
                if (s.integrations.isEmpty()) {
                    item {
                        Text(
                            stringResource(R.string.settings_no_integrations),
                            style = RcTypography.bodySmall,
                            color = rcColors.textMuted,
                            modifier = Modifier.padding(horizontal = Spacing.screenHorizontal),
                        )
                    }
                } else {
                    items(s.integrations) { integration ->
                        IntegrationRow(
                            integration = integration,
                            modifier = Modifier.padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.xs),
                        )
                    }
                }

                item { RcSectionHeader(title = stringResource(R.string.settings_developer_section)) }
                item {
                    RcCard(modifier = Modifier.padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.xs)) {
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                            PreferenceRow(label = stringResource(R.string.settings_base_url), value = s.baseUrl)
                            RcButton(
                                text = if (s.isSyncing) stringResource(R.string.settings_syncing) else stringResource(R.string.settings_sync_cta),
                                onClick = viewModel::sync,
                                isLoading = s.isSyncing,
                                style = RcButtonStyle.SECONDARY,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }

                if (s.isDirty) {
                    item {
                        RcButton(
                            text = stringResource(R.string.settings_save_cta),
                            onClick = viewModel::save,
                            isLoading = s.isSaving,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm),
                        )
                    }
                }

                if (s.message != null) {
                    item {
                        Text(
                            s.message,
                            style = RcTypography.bodySmall,
                            color = ZoneColors.base,
                            modifier = Modifier.padding(horizontal = Spacing.screenHorizontal),
                        )
                    }
                }

                if (s.error != null) {
                    item {
                        Text(
                            s.error,
                            style = RcTypography.bodySmall,
                            color = ZoneColors.interval,
                            modifier = Modifier.padding(horizontal = Spacing.screenHorizontal),
                        )
                    }
                }

                item {
                    RcButton(
                        text = stringResource(R.string.settings_sign_out),
                        onClick = viewModel::signOut,
                        style = RcButtonStyle.GHOST,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm),
                    )
                }
            }
        }
    }
}

@Composable
private fun PreferenceRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = RcTypography.bodyMedium, color = rcColors.text)
        Text(value, style = RcTypography.bodySmall, color = rcColors.textDim)
    }
}

@Composable
private fun IntegrationRow(integration: Integration, modifier: Modifier = Modifier) {
    RcCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                integration.displayName.ifBlank { integration.provider.replaceFirstChar { it.uppercase() } },
                style = RcTypography.bodyMedium,
                color = rcColors.text,
            )
            val statusZone = when (integration.status) {
                "active", "configured", "env_compat" -> "base"
                "error" -> "interval"
                else -> "rest"
            }
            RcZoneBadge(sessionType = statusZone)
        }
    }
}
