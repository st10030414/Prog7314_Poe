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

class VaultRepository(context: Context) {

    private val db: AppDatabase = AppDatabase.getDatabase(context.applicationContext)
    private val dao: VaultDao = db.vaultDao()
    private val crypto: CryptoHelper = CryptoHelper.getInstance(context.applicationContext)

    /** Observe decrypted notes for UI */
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

    private fun safeDecrypt(enc: String): String =
        try { crypto.decrypt(enc) } catch (_: Exception) { "•••" }

    /** Create */
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

    /** Update */
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

    /** Delete */
    suspend fun delete(id: Int) {
        val existing: VaultNote = dao.getById(id) ?: return
        dao.delete(existing)
    }
}
