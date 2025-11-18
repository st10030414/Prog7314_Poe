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
//(Client authentication, 2025).

class AuthViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val state: StateFlow<AuthUiState> = _state
    //(Client authentication, 2025).
    fun isLoggedIn(): Boolean = repo.isLoggedIn
    //(Client authentication, 2025).
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
    //(Client authentication, 2025).
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
    //(Client authentication, 2025).

    fun signOut() = repo.signOut()
    //(Client authentication, 2025).

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
    //(Client authentication, 2025).
}
/*
Reference List

Developer Android. 2025. Fragments, 10 February 2025. [Online]. Available at: https://developer.android.com/guide/fragments [Accessed 15 November 2025].

Developer Android. 2025. Save data in a local database using Room, 29 October 2025. [Online]. Available at: https://developer.android.com/training/data-storage/room [Accessed 15 November 2025].

Developer Android. 2025. Accessing data using Room DAOs, 10 February 2025. [Online]. Available at: https://developer.android.com/training/data-storage/room/accessing-data [Accessed 15 November 2025].

Developer Android. 2025. ViewModel overview, 3 September 2025. [Online]. Available at: https://developer.android.com/topic/libraries/architecture/viewmodel [Accessed 15 November 2025].

Developer Android. 2025. LiveData overview, 10 February 2025. [Online]. Available at: https://developer.android.com/topic/libraries/architecture/livedata#observe_livedata_objects [Accessed 15 November 2025].

Developer Android. 2025. Task scheduling, 8 September 2025. [Online]. Available at: https://developer.android.com/develop/background-work/background-tasks/persistent [Accessed 15 November 2025].

Developer Android. 2025. Navigation, 5 November 2025. [Online]. Available at: https://developer.android.com/guide/navigation [Accessed 15 November 2025].

Developer Android. 2025. ConstraintLayout, 17 July 2025. [Online]. Available at: https://developer.android.com/reference/androidx/constraintlayout/widget/ConstraintLayout [Accessed 15 November 2025].

Developer Android. 2025. Spinner, 17 September 2025. [Online]. Available at: https://developer.android.com/reference/android/widget/Spinner [Accessed 15 November 2025].

Developer Android. 2025. RecyclerView.Adapter, 15 May 2025. [Online]. Available at: https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter [Accessed 15 November 2025].

Developer Android. 2025. Add a floating action button, 30 October 2025. [Online]. Available at: https://developer.android.com/develop/ui/views/components/floating-action-button [Accessed 15 November 2025].

Developer Android. 2025. Better performance through threading, 3 January 2024. [Online]. Available at: https://developer.android.com/topic/performance/threads [Accessed 15 November 2025].

Developer Android. 2025. Kotlin coroutines on Android, 6 July 2024. [Online]. Available at: https://developer.android.com/kotlin/coroutines [Accessed 15 November 2025].

Firebase. 2025. Firebase Authentication, 20 October 2025. [Online]. Available at: https://firebase.google.com/docs/auth [Accessed 15 November 2025].

Firebase. 2025. Get Started with Firebase Authentication on Android, 14 November 2025. [Online]. Available at: https://firebase.google.com/docs/auth/android/start [Accessed 15 November 2025].

Firebase. 2025. Cloud Firestore, 14 November 2025. [Online]. Available at: https://firebase.google.com/docs/firestore [Accessed 15 November 2025].

Client authentication. 2025. 14 November 2025. [Online]. Available at: https://developers.google.com/android/guides/client-auth [Accessed 15 November 2025].
 */