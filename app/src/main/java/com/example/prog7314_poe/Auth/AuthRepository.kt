package com.example.prog7314_poe.auth

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

open class AuthRepository(
    private val authProvider: () -> FirebaseAuth = { FirebaseAuth.getInstance() }
) {
    protected val auth: FirebaseAuth by lazy { authProvider() }

    open val currentUser get() = auth.currentUser
    open val isLoggedIn get() = currentUser != null

    open suspend fun signIn(email: String, password: String): AuthResult =
        auth.signInWithEmailAndPassword(email.trim(), password).await()

    open suspend fun signUp(email: String, password: String): AuthResult =
        auth.createUserWithEmailAndPassword(email.trim(), password).await()

    open fun signOut() = auth.signOut()

    open fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }
}
