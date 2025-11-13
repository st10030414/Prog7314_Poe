package com.example.prog7314_poe.auth

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import org.mockito.Mockito

class TestAuthRepository
    : AuthRepository(authProvider = { error("Firebase should not be used in unit tests") }) {

    private val users = linkedMapOf<String, String>()
    private var uid: String? = null

    fun seedUser(email: String, password: String) {
        users[email.trim()] = password
    }

    override val currentUser: FirebaseUser? = null
    override val isLoggedIn: Boolean get() = uid != null

    override suspend fun signIn(email: String, password: String): AuthResult {
        val e = email.trim()
        val saved = users[e] ?: error("No user record found.")
        if (saved != password) error("Password is invalid.")
        uid = "uid_${e.hashCode()}"
        return dummyResult(uid!!)
    }

    override suspend fun signUp(email: String, password: String): AuthResult {
        val e = email.trim()
        if (users.containsKey(e)) error("Email already in use.")
        if (password.length < 6) error("Password too short")
        users[e] = password
        uid = "uid_${e.hashCode()}"
        return dummyResult(uid!!)
    }

    override fun signOut() { uid = null }

    private fun dummyResult(id: String): AuthResult {
        val user = Mockito.mock(FirebaseUser::class.java).apply {
            Mockito.`when`(uid).thenReturn(id)
            Mockito.`when`(isEmailVerified).thenReturn(true)
        }
        return Mockito.mock(AuthResult::class.java).apply {
            Mockito.`when`(user).thenReturn(user)
        }
    }
}
