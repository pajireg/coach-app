package com.suminchoi.coachapp.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suminchoi.coachapp.core.auth.AuthStore
import com.suminchoi.coachapp.core.model.AvailabilityRequest
import com.suminchoi.coachapp.core.model.GarminCredentialRequest
import com.suminchoi.coachapp.core.model.InjuryRequest
import com.suminchoi.coachapp.core.model.Integration
import com.suminchoi.coachapp.core.model.UpdatePreferencesRequest
import com.suminchoi.coachapp.core.model.User
import com.suminchoi.coachapp.core.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(
        val user: User,
        val integrations: List<Integration>,
        val baseUrl: String,
        val isSaving: Boolean = false,
        val isSyncing: Boolean = false,
        val isAvailabilitySaving: Boolean = false,
        val isInjurySaving: Boolean = false,
        val isGarminSaving: Boolean = false,
        val isGarminDisconnecting: Boolean = false,
        val isDirty: Boolean = false,
        val garminEmail: String = "",
        val garminPassword: String = "",
        val availabilityWeekday: Int = 5,
        val availabilityMaxMinutes: String = "90",
        val availabilitySessionType: String = "long_run",
        val injuryArea: String = "",
        val injurySeverity: String = "3",
        val injuryNotes: String = "",
        val message: String? = null,
        val error: String? = null,
    ) : SettingsUiState
    data class Error(val message: String) : SettingsUiState
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val api: ApiService,
    private val authStore: AuthStore,
) : ViewModel() {

    private val _state = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val state = _state.asStateFlow()

    private var editedUser: User? = null

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = SettingsUiState.Loading
            try {
                val user = api.getMe()
                val integrations = api.getIntegrations().integrations
                editedUser = user
                _state.value = SettingsUiState.Success(user, integrations, authStore.baseUrl)
            } catch (e: Exception) {
                _state.value = SettingsUiState.Error(e.message ?: "알 수 없는 오류")
            }
        }
    }

    fun updateTimezone(v: String) = mutate { copy(preferences = preferences.copy(timezone = v)) }
    fun updateLocale(v: String) = mutate { copy(preferences = preferences.copy(locale = v)) }
    fun updateRunMode(v: String) = mutate { copy(preferences = preferences.copy(runMode = v)) }
    fun updateIncludeStrength(v: Boolean) = mutate { copy(preferences = preferences.copy(includeStrength = v)) }
    fun updateAvailabilityWeekday(v: Int) = updateSuccess { copy(availabilityWeekday = v, message = null, error = null) }
    fun updateAvailabilityMaxMinutes(v: String) = updateSuccess { copy(availabilityMaxMinutes = v, message = null, error = null) }
    fun updateAvailabilitySessionType(v: String) = updateSuccess { copy(availabilitySessionType = v, message = null, error = null) }
    fun updateGarminEmail(v: String) = updateSuccess { copy(garminEmail = v, message = null, error = null) }
    fun updateGarminPassword(v: String) = updateSuccess { copy(garminPassword = v, message = null, error = null) }
    fun updateInjuryArea(v: String) = updateSuccess { copy(injuryArea = v, message = null, error = null) }
    fun updateInjurySeverity(v: String) = updateSuccess { copy(injurySeverity = v, message = null, error = null) }
    fun updateInjuryNotes(v: String) = updateSuccess { copy(injuryNotes = v, message = null, error = null) }

    private fun mutate(block: User.() -> User) {
        val current = editedUser ?: return
        editedUser = current.block()
        val s = _state.value as? SettingsUiState.Success ?: return
        _state.value = s.copy(user = editedUser!!, isDirty = true)
    }

    private fun updateSuccess(block: SettingsUiState.Success.() -> SettingsUiState.Success) {
        val s = _state.value as? SettingsUiState.Success ?: return
        _state.value = s.block()
    }

    fun save() {
        val s = _state.value as? SettingsUiState.Success ?: return
        val user = editedUser ?: return
        viewModelScope.launch {
            _state.value = s.copy(isSaving = true)
            try {
                val updated = api.updatePreferences(
                    UpdatePreferencesRequest(
                        timezone = user.preferences.timezone,
                        locale = user.preferences.locale,
                        runMode = user.preferences.runMode,
                        includeStrength = user.preferences.includeStrength,
                    )
                )
                editedUser = updated
                _state.value = s.copy(user = updated, isSaving = false, isDirty = false)
            } catch (e: Exception) {
                _state.value = s.copy(isSaving = false, error = e.message)
            }
        }
    }

    fun sync() {
        val s = _state.value as? SettingsUiState.Success ?: return
        viewModelScope.launch {
            _state.value = s.copy(isSyncing = true)
            try {
                api.triggerSync(com.suminchoi.coachapp.core.model.SyncRequest())
                _state.value = s.copy(isSyncing = false)
            } catch (e: Exception) {
                _state.value = s.copy(isSyncing = false, error = e.message)
            }
        }
    }

    fun saveAvailability() {
        val s = _state.value as? SettingsUiState.Success ?: return
        viewModelScope.launch {
            _state.value = s.copy(isAvailabilitySaving = true, message = null, error = null)
            try {
                api.submitAvailability(
                    AvailabilityRequest(
                        weekday = s.availabilityWeekday,
                        maxDurationMinutes = s.availabilityMaxMinutes.toIntOrNull(),
                        preferredSessionType = s.availabilitySessionType.trim().ifBlank { null },
                    )
                )
                _state.value = s.copy(isAvailabilitySaving = false, message = "가용 시간이 저장되었습니다.")
            } catch (e: Exception) {
                _state.value = s.copy(isAvailabilitySaving = false, error = e.message ?: "가용 시간 저장에 실패했습니다.")
            }
        }
    }

    fun connectGarmin() {
        val s = _state.value as? SettingsUiState.Success ?: return
        if (s.garminEmail.isBlank() || s.garminPassword.isBlank()) {
            _state.value = s.copy(error = "가민 이메일과 비밀번호를 입력해주세요.", message = null)
            return
        }
        viewModelScope.launch {
            _state.value = s.copy(isGarminSaving = true, message = null, error = null)
            try {
                val integrations = api.connectGarmin(
                    GarminCredentialRequest(
                        email = s.garminEmail.trim(),
                        password = s.garminPassword,
                    )
                ).integrations
                _state.value = s.copy(
                    integrations = integrations,
                    isGarminSaving = false,
                    garminPassword = "",
                    message = "가민 연결 정보가 저장되었습니다.",
                )
            } catch (e: Exception) {
                _state.value = s.copy(isGarminSaving = false, error = e.message ?: "가민 연결에 실패했습니다.")
            }
        }
    }

    fun disconnectGarmin() {
        val s = _state.value as? SettingsUiState.Success ?: return
        viewModelScope.launch {
            _state.value = s.copy(isGarminDisconnecting = true, message = null, error = null)
            try {
                val integrations = api.disconnectGarmin().integrations
                _state.value = s.copy(
                    integrations = integrations,
                    isGarminDisconnecting = false,
                    message = "가민 연결이 해제되었습니다.",
                )
            } catch (e: Exception) {
                _state.value = s.copy(isGarminDisconnecting = false, error = e.message ?: "가민 연결 해제에 실패했습니다.")
            }
        }
    }

    fun saveInjury() {
        val s = _state.value as? SettingsUiState.Success ?: return
        if (s.injuryArea.isBlank()) {
            _state.value = s.copy(error = "부상 부위를 입력해주세요.", message = null)
            return
        }
        viewModelScope.launch {
            _state.value = s.copy(isInjurySaving = true, message = null, error = null)
            try {
                api.submitInjury(
                    InjuryRequest(
                        statusDate = LocalDate.now().toString(),
                        injuryArea = s.injuryArea.trim(),
                        severity = s.injurySeverity.toIntOrNull()?.coerceIn(1, 10) ?: 3,
                        notes = s.injuryNotes.trim().ifBlank { null },
                    )
                )
                _state.value = s.copy(isInjurySaving = false, message = "부상 상태가 저장되었습니다.")
            } catch (e: Exception) {
                _state.value = s.copy(isInjurySaving = false, error = e.message ?: "부상 상태 저장에 실패했습니다.")
            }
        }
    }

    fun signOut() = authStore.signOut()
}
