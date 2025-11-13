package com.example.prog7314_poe.Notes

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        Note::class,
        VaultNote::class
    ],
    version = 2,              // bumped from 1 -> 2
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun vaultDao(): VaultDao   // NEW

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration 1 -> 2: create the vault_notes table
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS vault_notes(
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        contentEnc TEXT NOT NULL,
                        tags TEXT,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notes.db"
                )
                    .addMigrations(MIGRATION_1_2) // keep user data; add vault table
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
