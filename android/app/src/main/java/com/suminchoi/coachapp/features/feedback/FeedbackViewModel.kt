package com.suminchoi.coachapp.features.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suminchoi.coachapp.core.model.FeedbackRequest
import com.suminchoi.coachapp.core.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class FeedbackUiState(
    val feedbackDate: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
    val fatigue: Int? = null,
    val soreness: Int? = null,
    val stress: Int? = null,
    val motivation: Int? = null,
    val sleep: Int? = null,
    val painNotes: String = "",
    val notes: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val submitted: Boolean = false,
)

@HiltViewModel
class FeedbackViewModel @Inject constructor(private val api: ApiService) : ViewModel() {

    private val _state = MutableStateFlow(FeedbackUiState())
    val state = _state.asStateFlow()

    fun update(block: FeedbackUiState.() -> FeedbackUiState) {
        _state.value = _state.value.block()
    }

    fun submit(onSuccess: () -> Unit) {
        val s = _state.value
        viewModelScope.launch {
            _state.value = s.copy(isLoading = true, error = null)
            try {
                api.submitFeedback(
                    FeedbackRequest(
                        feedbackDate = s.feedbackDate,
                        fatigue = s.fatigue,
                        soreness = s.soreness,
                        stress = s.stress,
                        motivation = s.motivation,
                        sleep = s.sleep,
                        painNotes = s.painNotes.ifBlank { null },
                        notes = s.notes.ifBlank { null },
                    )
                )
                _state.value = FeedbackUiState()
                onSuccess()
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message ?: "오류가 발생했습니다.")
            }
        }
    }
}
