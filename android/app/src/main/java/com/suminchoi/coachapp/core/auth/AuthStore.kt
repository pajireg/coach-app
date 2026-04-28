package com.suminchoi.coachapp.core.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthState {
    data object SignedOut : AuthState()
    data class SignedIn(val apiKey: String, val baseUrl: String) : AuthState()
}

@Singleton
class AuthStore @Inject constructor(private val storage: SecureStorage) {

    private val _state = MutableStateFlow(storage.load())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    val apiKey: String? get() = (_state.value as? AuthState.SignedIn)?.apiKey
    val baseUrl: String get() = (_state.value as? AuthState.SignedIn)?.baseUrl ?: "http://10.0.2.2:8624"

    fun save(apiKey: String, baseUrl: String) {
        storage.save(apiKey, baseUrl)
        _state.value = AuthState.SignedIn(apiKey, baseUrl)
    }

    fun signOut() {
        storage.clear()
        _state.value = AuthState.SignedOut
    }
}
