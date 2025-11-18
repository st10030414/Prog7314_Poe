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

class CryptoHelper private constructor(private val secretKey: SecretKey) {

    companion object {
        private const val AES_MODE = "AES/GCM/NoPadding"
        private const val KEY_ALIAS = MasterKey.DEFAULT_MASTER_KEY_ALIAS // Keystore alias
        private const val KEY_SIZE = 256
        private const val IV_BYTES = 12
        //(Developer Android, 2025).

        fun getInstance(context: Context): CryptoHelper {
            val masterKey = MasterKey.Builder(context, KEY_ALIAS)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

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
            //(Developer Android, 2025).
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
        //(Developer Android, 2025).
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
        //(Developer Android, 2025).
    }
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