package com.example.prog7314_poe.Notes

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultDao {
    @Query("SELECT * FROM vault_notes ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<VaultNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: VaultNote): Long

    @Update
    suspend fun update(note: VaultNote)

    @Delete
    suspend fun delete(note: VaultNote)

    @Query("SELECT * FROM vault_notes WHERE id = :id")
    suspend fun getById(id: Int): VaultNote?
}
