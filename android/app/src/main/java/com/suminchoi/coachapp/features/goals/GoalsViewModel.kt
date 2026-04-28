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
    val goalName: String = "",
    val distance: String = "",
    val raceDate: String = "",
    val goalTime: String = "",
    val targetPace: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val submitted: Boolean = false,
)

@HiltViewModel
class GoalsViewModel @Inject constructor(private val api: ApiService) : ViewModel() {

    private val _form = MutableStateFlow(GoalFormState())
    val form = _form.asStateFlow()

    fun updateGoalName(v: String) { _form.value = _form.value.copy(goalName = v, error = null, submitted = false) }
    fun updateDistance(v: String) { _form.value = _form.value.copy(distance = v, error = null, submitted = false) }
    fun updateDate(v: String) { _form.value = _form.value.copy(raceDate = v, error = null, submitted = false) }
    fun updateGoalTime(v: String) { _form.value = _form.value.copy(goalTime = v, error = null, submitted = false) }
    fun updateTargetPace(v: String) { _form.value = _form.value.copy(targetPace = v, error = null, submitted = false) }

    fun submit() {
        val s = _form.value
        if (s.goalName.isBlank()) {
            _form.value = s.copy(error = "목표 이름을 입력해주세요.")
            return
        }
        viewModelScope.launch {
            _form.value = s.copy(isLoading = true, error = null)
            try {
                api.submitGoal(
                    GoalRequest(
                        goalName = s.goalName.trim(),
                        raceDate = s.raceDate.trim().ifBlank { null },
                        distance = s.distance.trim().ifBlank { null },
                        goalTime = s.goalTime.trim().ifBlank { null },
                        targetPace = s.targetPace.trim().ifBlank { null },
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
