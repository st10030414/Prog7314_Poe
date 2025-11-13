package com.example.prog7314_poe.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecurePrefs {
    private const val FILE = "vault_secure_prefs"
    private const val KEY_PIN = "vault_pin"

    private fun prefs(context: Context) = EncryptedSharedPreferences.create(
        context,
        FILE,
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getPin(context: Context): String? = prefs(context).getString(KEY_PIN, null)

    fun setPin(context: Context, pin: String) {
        prefs(context).edit().putString(KEY_PIN, pin).apply()
    }
}
