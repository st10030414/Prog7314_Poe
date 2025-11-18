package com.example.prog7314_poe.Offline

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [OfflineNote::class], version = 1, exportSchema = false)
abstract class OfflineDatabase : RoomDatabase() {

    abstract fun offlineNoteDao(): OfflineNoteDao

    companion object {
        @Volatile private var INSTANCE: OfflineDatabase? = null

        fun getInstance(context: Context): OfflineDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    OfflineDatabase::class.java,
                    "offline_notes.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
