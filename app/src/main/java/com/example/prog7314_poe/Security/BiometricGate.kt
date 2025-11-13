package com.example.prog7314_poe.security

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

class BiometricGate(
    private val activity: AppCompatActivity,
    private val title: String = "Unlock Vault",
    private val subtitle: String = "Authenticate to access secure notes"
) {
    interface Callback {
        fun onAuthenticated()
        fun onFailed(errMsg: String)
        fun onUnavailable()
    }

    fun canAuth(context: Context): Boolean {
        val bm = BiometricManager.from(context)
        val res = bm.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
        return res == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun prompt(callback: Callback) {
        val executor = ContextCompat.getMainExecutor(activity)
        val bp = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(code: Int, errString: CharSequence) {
                callback.onFailed(errString.toString())
            }
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                callback.onAuthenticated()
            }
            override fun onAuthenticationFailed() {
                callback.onFailed("Authentication failed")
            }
        })
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()
        bp.authenticate(info)
    }
}
