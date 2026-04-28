package com.suminchoi.coachapp.features.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suminchoi.coachapp.core.auth.AuthStore
import com.suminchoi.coachapp.core.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class OnboardingUiState(
    val apiKey: String = "",
    val baseUrl: String = "http://10.0.2.2:8624",
    val isLoading: Boolean = false,
    val status: String? = null,
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
        _state.value = _state.value.copy(apiKey = value, status = null, error = null)
    }

    fun updateBaseUrl(value: String) {
        _state.value = _state.value.copy(baseUrl = value, status = null, error = null)
    }

    fun submit(onSuccess: () -> Unit) {
        val s = _state.value
        val apiKey = s.apiKey.trim()
        val baseUrl = s.baseUrl.trim().ifBlank { "http://10.0.2.2:8624" }
        if (apiKey.isBlank()) {
            _state.value = s.copy(status = null, error = "API 키를 입력해주세요.")
            return
        }
        if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
            _state.value = s.copy(status = null, error = "서버 주소는 http:// 또는 https://로 시작해야 합니다.")
            return
        }
        viewModelScope.launch {
            _state.value = s.copy(isLoading = true, status = "서버 연결을 확인하는 중입니다.", error = null)
            try {
                withTimeout(8_000) {
                    api.getMe("Bearer $apiKey", baseUrl)
                }
                authStore.save(apiKey, baseUrl)
                onSuccess()
            } catch (e: HttpException) {
                authStore.signOut()
                _state.value = _state.value.copy(
                    isLoading = false,
                    status = null,
                    error = when (e.code()) {
                        401 -> "API 키가 올바르지 않습니다."
                        else -> "서버 오류가 발생했습니다. (${e.code()})"
                    },
                )
            } catch (e: TimeoutCancellationException) {
                authStore.signOut()
                _state.value = _state.value.copy(
                    isLoading = false,
                    status = null,
                    error = "서버 응답 시간이 초과되었습니다.",
                )
            } catch (e: IOException) {
                authStore.signOut()
                _state.value = _state.value.copy(
                    isLoading = false,
                    status = null,
                    error = "서버에 연결할 수 없습니다. 주소와 네트워크를 확인해주세요.",
                )
            } catch (e: Exception) {
                authStore.signOut()
                _state.value = _state.value.copy(
                    isLoading = false,
                    status = null,
                    error = e.message ?: "연결에 실패했습니다. (${e::class.simpleName})",
                )
            }
        }
    }
}
