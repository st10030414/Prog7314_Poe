package com.example.prog7314_poe.Notes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String? = null,
    val content: String,
    val tags: String? = null
)
