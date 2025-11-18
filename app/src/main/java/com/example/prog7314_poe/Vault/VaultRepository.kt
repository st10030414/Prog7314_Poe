package com.example.prog7314_poe.Vault

import android.content.Context
import com.example.prog7314_poe.crypto.CryptoHelper
import com.example.prog7314_poe.Notes.AppDatabase
import com.example.prog7314_poe.Notes.VaultDao
import com.example.prog7314_poe.Notes.VaultNote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class VaultNoteUi(
    val id: Int,
    val title: String,
    val content: String,
    val tags: String?,
    val updatedAt: Long
)
//(Developer Android, 2025).

class VaultRepository(context: Context) {

    private val db: AppDatabase = AppDatabase.getDatabase(context.applicationContext)
    private val dao: VaultDao = db.vaultDao()
    private val crypto: CryptoHelper = CryptoHelper.getInstance(context.applicationContext)
    //(Developer Android, 2025).
    fun observeNotes(): Flow<List<VaultNoteUi>> =
        dao.observeAll().map { list ->
            list.map { vn: VaultNote ->
                VaultNoteUi(
                    id = vn.id,
                    title = vn.title,
                    content = safeDecrypt(vn.contentEnc),
                    tags = vn.tags,
                    updatedAt = vn.updatedAt
                )
            }
        }
    //(Developer Android, 2025).
    private fun safeDecrypt(enc: String): String =
        try { crypto.decrypt(enc) } catch (_: Exception) { "•••" }
    //(Developer Android, 2025).
    suspend fun add(title: String, content: String, tags: String?) {
        val now = System.currentTimeMillis()
        val enc = crypto.encrypt(content)
        val entity = VaultNote(
            title = title.ifBlank { "(Untitled)" },
            contentEnc = enc,
            tags = tags,
            createdAt = now,
            updatedAt = now
        )
        dao.insert(entity)
    }
    //(Developer Android, 2025).
    suspend fun update(id: Int, title: String, content: String, tags: String?) {
        val existing: VaultNote = dao.getById(id) ?: return
        val enc = crypto.encrypt(content)
        val updated = existing.copy(
            title = title.ifBlank { "(Untitled)" },
            contentEnc = enc,
            tags = tags,
            updatedAt = System.currentTimeMillis()
        )
        dao.update(updated)
    }
    //(Developer Android, 2025).
    suspend fun delete(id: Int) {
        val existing: VaultNote = dao.getById(id) ?: return
        dao.delete(existing)
    }
    //(Developer Android, 2025).
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