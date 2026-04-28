package com.suminchoi.coachapp.features.trends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suminchoi.coachapp.core.model.TrendsResponse
import com.suminchoi.coachapp.core.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TrendsUiState {
    data object Loading : TrendsUiState
    data class Success(val trends: TrendsResponse) : TrendsUiState
    data class Error(val message: String) : TrendsUiState
}

@HiltViewModel
class TrendsViewModel @Inject constructor(private val api: ApiService) : ViewModel() {

    private val _state = MutableStateFlow<TrendsUiState>(TrendsUiState.Loading)
    val state = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = TrendsUiState.Loading
            _state.value = try {
                TrendsUiState.Success(api.getTrends())
            } catch (e: Exception) {
                TrendsUiState.Error(e.message ?: "알 수 없는 오류")
            }
        }
    }
}
