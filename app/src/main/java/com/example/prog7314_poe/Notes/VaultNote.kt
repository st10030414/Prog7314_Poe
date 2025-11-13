package com.example.prog7314_poe.Notes

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * contentEnc holds AES-GCM encrypted Base64 text (never store plaintext here).
 */
@Entity(tableName = "vault_notes")
data class VaultNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val contentEnc: String,
    val tags: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
