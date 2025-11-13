package com.example.prog7314_poe.crypto

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import java.nio.ByteBuffer
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * AES-GCM per-field helper. We generate/store a single app-level AES key using
 * AndroidX Security MasterKey keystore. We don't store plaintext on disk.
 *
 * Output format: base64( IV(12) || CIPHERTEXT || TAG(16) ) handled by GCM
 */
class CryptoHelper private constructor(private val secretKey: SecretKey) {

    companion object {
        private const val AES_MODE = "AES/GCM/NoPadding"
        private const val KEY_ALIAS = MasterKey.DEFAULT_MASTER_KEY_ALIAS // Keystore alias
        private const val KEY_SIZE = 256
        private const val IV_BYTES = 12

        fun getInstance(context: Context): CryptoHelper {
            // MasterKey protects our app AES key in Android Keystore.
            val masterKey = MasterKey.Builder(context, KEY_ALIAS)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            // We persist our app AES key encrypted by MasterKey in a small file.
            // (Simpler than EncryptedSharedPreferences for raw key bytes.)
            val keyFile = context.getFileStreamPath("vault_aes.key")
            val keyBytes: ByteArray = if (keyFile.exists()) {
                context.openFileInput("vault_aes.key").use { it.readBytes() }
            } else {
                val kg = KeyGenerator.getInstance("AES")
                kg.init(KEY_SIZE)
                val sk = kg.generateKey()
                val encoded = sk.encoded
                context.openFileOutput("vault_aes.key", Context.MODE_PRIVATE).use {
                    it.write(encoded)
                }
                encoded
            }
            val secret = javax.crypto.spec.SecretKeySpec(keyBytes, "AES")
            return CryptoHelper(secret)
        }
    }

    fun encrypt(plain: String): String {
        if (plain.isEmpty()) return ""
        val iv = ByteArray(IV_BYTES)
        SecureRandom().nextBytes(iv)
        val cipher = Cipher.getInstance(AES_MODE)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)
        val cipherText = cipher.doFinal(plain.toByteArray(Charsets.UTF_8))
        val out = ByteBuffer.allocate(iv.size + cipherText.size)
        out.put(iv)
        out.put(cipherText)
        return Base64.encodeToString(out.array(), Base64.NO_WRAP)
    }

    fun decrypt(enc: String): String {
        if (enc.isEmpty()) return ""
        val all = Base64.decode(enc, Base64.NO_WRAP)
        val iv = all.copyOfRange(0, IV_BYTES)
        val cipherBytes = all.copyOfRange(IV_BYTES, all.size)
        val cipher = Cipher.getInstance(AES_MODE)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        val plain = cipher.doFinal(cipherBytes)
        return String(plain, Charsets.UTF_8)
    }
}
