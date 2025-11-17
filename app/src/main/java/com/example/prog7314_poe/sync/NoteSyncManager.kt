package com.example.prog7314_poe.sync

import android.content.Context
import com.example.prog7314_poe.Notes.Note
import com.example.prog7314_poe.Notes.NoteDao
import com.example.prog7314_poe.Notes.AppDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object NoteSyncManager {

    private val firestore = FirebaseFirestore.getInstance()

    fun syncNow(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            val noteDao: NoteDao = db.noteDao()

            val pendingNotes = noteDao.getPendingSyncNotes()
            if (pendingNotes.isEmpty()) return@launch

            for (note in pendingNotes) {
                try {
                    if (note.firebaseId == null) {
                        // FIRST-TIME UPLOAD
                        val docRef = firestore.collection("notes").document()
                        docRef.set(note.toFirestoreMap()).await()
                        noteDao.updateFirebaseId(note.id, docRef.id)
                    } else {
                        // UPDATE EXISTING REMOTE DOC
                        firestore.collection("notes")
                            .document(note.firebaseId)
                            .set(note.toFirestoreMap())
                            .await()
                    }

                    noteDao.markSynced(note.id)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}

// Helper extension to convert Note → Firestore-friendly map
fun Note.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "localId" to id,
        "title" to title,
        "content" to content,
        "tags" to tags,
        "updatedAt" to updatedAt
    )
}
