package com.example.prog7314_poe.Offline

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface OfflineNoteDao {

    @Query("SELECT * FROM offline_notes ORDER BY id DESC")
    fun getAllNotes(): LiveData<List<OfflineNote>>

    @Query("SELECT * FROM offline_notes WHERE id = :id")
    suspend fun getNoteById(id: Int): OfflineNote?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: OfflineNote)

    @Update
    suspend fun update(note: OfflineNote)
}
