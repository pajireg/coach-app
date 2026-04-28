package com.suminchoi.coachapp.features.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suminchoi.coachapp.core.model.GoalRequest
import com.suminchoi.coachapp.core.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GoalFormState(
    val goalType: String = "race",
    val targetDistanceKm: String = "",
    val targetDate: String = "",
    val notes: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val submitted: Boolean = false,
)

@HiltViewModel
class GoalsViewModel @Inject constructor(private val api: ApiService) : ViewModel() {

    private val _form = MutableStateFlow(GoalFormState())
    val form = _form.asStateFlow()

    fun updateGoalType(v: String) { _form.value = _form.value.copy(goalType = v) }
    fun updateDistance(v: String) { _form.value = _form.value.copy(targetDistanceKm = v) }
    fun updateDate(v: String) { _form.value = _form.value.copy(targetDate = v) }
    fun updateNotes(v: String) { _form.value = _form.value.copy(notes = v) }

    fun submit() {
        val s = _form.value
        viewModelScope.launch {
            _form.value = s.copy(isLoading = true, error = null)
            try {
                api.submitGoal(
                    GoalRequest(
                        goalType = s.goalType,
                        targetDistanceKm = s.targetDistanceKm.toDoubleOrNull(),
                        targetDate = s.targetDate.ifBlank { null },
                        notes = s.notes.ifBlank { null },
                    )
                )
                _form.value = _form.value.copy(isLoading = false, submitted = true)
            } catch (e: Exception) {
                _form.value = _form.value.copy(isLoading = false, error = e.message ?: "오류가 발생했습니다.")
            }
        }
    }

    fun dismissSuccess() {
        _form.value = _form.value.copy(submitted = false)
    }
}
