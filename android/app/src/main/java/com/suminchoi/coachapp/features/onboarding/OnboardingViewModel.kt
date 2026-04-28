package com.suminchoi.coachapp.features.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suminchoi.coachapp.core.auth.AuthStore
import com.suminchoi.coachapp.core.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val apiKey: String = "",
    val baseUrl: String = "http://10.0.2.2:8000",
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val authStore: AuthStore,
    private val api: ApiService,
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiState())
    val state = _state.asStateFlow()

    fun updateApiKey(value: String) {
        _state.value = _state.value.copy(apiKey = value, error = null)
    }

    fun updateBaseUrl(value: String) {
        _state.value = _state.value.copy(baseUrl = value, error = null)
    }

    fun submit(onSuccess: () -> Unit) {
        val s = _state.value
        if (s.apiKey.isBlank()) {
            _state.value = s.copy(error = "API 키를 입력해주세요.")
            return
        }
        viewModelScope.launch {
            _state.value = s.copy(isLoading = true, error = null)
            try {
                authStore.save(s.apiKey.trim(), s.baseUrl.trim().ifBlank { "http://10.0.2.2:8000" })
                onSuccess()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "연결에 실패했습니다.",
                )
            }
        }
    }
}
