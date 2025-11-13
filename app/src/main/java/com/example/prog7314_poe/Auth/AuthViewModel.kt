package com.example.prog7314_poe.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val uid: String) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val state: StateFlow<AuthUiState> = _state

    fun isLoggedIn(): Boolean = repo.isLoggedIn

    fun signIn(email: String, password: String) {
        _state.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                val res = repo.signIn(email, password)
                _state.value = AuthUiState.Success(res.user?.uid.orEmpty())
            } catch (e: Exception) {
                _state.value = AuthUiState.Error(userMessage(e))
            }
        }
    }

    fun signUp(email: String, password: String) {
        _state.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                val res = repo.signUp(email, password)
                _state.value = AuthUiState.Success(res.user?.uid.orEmpty())
            } catch (e: Exception) {
                _state.value = AuthUiState.Error(userMessage(e))
            }
        }
    }

    fun signOut() = repo.signOut()

    private fun userMessage(e: Exception): String {
        val msg = e.message?.lowercase().orEmpty()
        return when {
            "password" in msg && "short" in msg -> "Password too short (min 6 characters)."
            "already in use" in msg -> "Email already in use."
            "badly formatted" in msg || "email" in msg && "invalid" in msg -> "Invalid email address."
            "no user record" in msg || "password is invalid" in msg -> "Incorrect email or password."
            else -> e.message ?: "Authentication failed."
        }
    }
}
