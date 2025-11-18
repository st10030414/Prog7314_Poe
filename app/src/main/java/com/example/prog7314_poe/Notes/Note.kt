package com.example.prog7314_poe.Notes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String?,
    val content: String,
    val tags: String?,
    val updatedAt: Long = System.currentTimeMillis(),
    val pendingSync: Boolean = true,
    val firebaseId: String? = null
)
