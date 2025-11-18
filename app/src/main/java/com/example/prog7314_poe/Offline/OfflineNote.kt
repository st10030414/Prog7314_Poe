package com.example.prog7314_poe.Offline

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_notes")
data class OfflineNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String = "",
    val content: String = "",
    val tags: String = ""
)
