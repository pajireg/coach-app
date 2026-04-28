package com.suminchoi.coachapp.features.weekly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suminchoi.coachapp.core.model.DashboardResponse
import com.suminchoi.coachapp.core.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface WeeklyUiState {
    data object Loading : WeeklyUiState
    data class Success(val dashboard: DashboardResponse) : WeeklyUiState
    data class Error(val message: String) : WeeklyUiState
}

@HiltViewModel
class WeeklyViewModel @Inject constructor(private val api: ApiService) : ViewModel() {

    private val _state = MutableStateFlow<WeeklyUiState>(WeeklyUiState.Loading)
    val state = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = WeeklyUiState.Loading
            _state.value = try {
                WeeklyUiState.Success(api.getDashboard())
            } catch (e: Exception) {
                WeeklyUiState.Error(e.message ?: "알 수 없는 오류")
            }
        }
    }
}
