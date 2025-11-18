package com.example.prog7314_poe.Notes

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long

    @Update
    suspend fun update(note: Note)

    @Query("SELECT * FROM notes")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :noteId LIMIT 1")
    suspend fun getNoteById(noteId: Int): Note?

    @Query("SELECT * FROM notes WHERE pendingSync = 1")
    suspend fun getPendingSyncNotes(): List<Note>

    @Query("UPDATE notes SET pendingSync = 0 WHERE id = :id")
    suspend fun markSynced(id: Int)

    @Query("UPDATE notes SET firebaseId = :firebaseId WHERE id = :id")
    suspend fun updateFirebaseId(id: Int, firebaseId: String)
}
