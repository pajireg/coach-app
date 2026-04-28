package com.suminchoi.coachapp.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suminchoi.coachapp.core.auth.AuthStore
import com.suminchoi.coachapp.core.model.Integration
import com.suminchoi.coachapp.core.model.UpdatePreferencesRequest
import com.suminchoi.coachapp.core.model.User
import com.suminchoi.coachapp.core.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(
        val user: User,
        val integrations: List<Integration>,
        val isSaving: Boolean = false,
        val isSyncing: Boolean = false,
        val isDirty: Boolean = false,
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
                _state.value = SettingsUiState.Success(user, integrations)
            } catch (e: Exception) {
                _state.value = SettingsUiState.Error(e.message ?: "알 수 없는 오류")
            }
        }
    }

    fun updateTimezone(v: String) = mutate { copy(preferences = preferences.copy(timezone = v)) }
    fun updateLocale(v: String) = mutate { copy(preferences = preferences.copy(locale = v)) }
    fun updateRunMode(v: String) = mutate { copy(preferences = preferences.copy(runMode = v)) }
    fun updateIncludeStrength(v: Boolean) = mutate { copy(preferences = preferences.copy(includeStrength = v)) }

    private fun mutate(block: User.() -> User) {
        val current = editedUser ?: return
        editedUser = current.block()
        val s = _state.value as? SettingsUiState.Success ?: return
        _state.value = s.copy(user = editedUser!!, isDirty = true)
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

    fun signOut() = authStore.signOut()
}
