package com.example.prog7314_poe

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.example.prog7314_poe.Offline.OfflineDatabase
import com.example.prog7314_poe.Offline.OfflineNote
import com.example.prog7314_poe.R
import jp.wasabeef.richeditor.RichEditor
import kotlinx.coroutines.*

class OfflineNoteFragment : Fragment() {

    private lateinit var title: EditText
    private lateinit var editor: RichEditor
    private lateinit var tagSpinner: Spinner
    private lateinit var backBtn: ImageButton
    //(Developer Android, 2025).
    private var noteId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noteId = arguments?.getInt("note_id")
    }
    //(Developer Android, 2025).
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_note, container, false)

        title = view.findViewById(R.id.text_title)
        editor = view.findViewById(R.id.text_content)
        tagSpinner = view.findViewById(R.id.tag_selection)
        backBtn = view.findViewById(R.id.button_back)

        loadNote()
        //(Developer Android, 2025).
        val saveAndBack = {
            val contentHtml = editor.html ?: ""
            saveNote(title.text.toString(), contentHtml, tagSpinner.selectedItem.toString())
            requireActivity().supportFragmentManager.popBackStack()
        }
        //(Developer Android, 2025).
        backBtn.setOnClickListener { saveAndBack() }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { saveAndBack() }

        return view
    }

    private fun loadNote() {
        val db = OfflineDatabase.getInstance(requireContext())

        CoroutineScope(Dispatchers.IO).launch {
            val note = noteId?.let { db.offlineNoteDao().getNoteById(it) }

            withContext(Dispatchers.Main) {
                note?.let {
                    title.setText(it.title)
                    editor.html = it.content
                }
            }
        }
    }
    //(Developer Android, 2025).
    private fun saveNote(titleText: String, contentHtml: String, tag: String) {
        val db = OfflineDatabase.getInstance(requireContext())
        val dao = db.offlineNoteDao()

        CoroutineScope(Dispatchers.IO).launch {
            if (noteId != null) {
                val old = dao.getNoteById(noteId!!)
                old?.let { dao.update(it.copy(title = titleText, content = contentHtml, tags = tag)) }
            } else {
                dao.insert(OfflineNote(title = titleText, content = contentHtml, tags = tag))
            }
        }
    }
    //(Developer Android, 2025).
}
/*
Reference List

Developer Android. 2025. Fragments, 10 February 2025. [Online]. Available at: https://developer.android.com/guide/fragments [Accessed 15 November 2025].

Developer Android. 2025. Save data in a local database using Room, 29 October 2025. [Online]. Available at: https://developer.android.com/training/data-storage/room [Accessed 15 November 2025].

Developer Android. 2025. Accessing data using Room DAOs, 10 February 2025. [Online]. Available at: https://developer.android.com/training/data-storage/room/accessing-data [Accessed 15 November 2025].

Developer Android. 2025. ViewModel overview, 3 September 2025. [Online]. Available at: https://developer.android.com/topic/libraries/architecture/viewmodel [Accessed 15 November 2025].

Developer Android. 2025. LiveData overview, 10 February 2025. [Online]. Available at: https://developer.android.com/topic/libraries/architecture/livedata#observe_livedata_objects [Accessed 15 November 2025].

Developer Android. 2025. Task scheduling, 8 September 2025. [Online]. Available at: https://developer.android.com/develop/background-work/background-tasks/persistent [Accessed 15 November 2025].

Developer Android. 2025. Navigation, 5 November 2025. [Online]. Available at: https://developer.android.com/guide/navigation [Accessed 15 November 2025].

Developer Android. 2025. ConstraintLayout, 17 July 2025. [Online]. Available at: https://developer.android.com/reference/androidx/constraintlayout/widget/ConstraintLayout [Accessed 15 November 2025].

Developer Android. 2025. Spinner, 17 September 2025. [Online]. Available at: https://developer.android.com/reference/android/widget/Spinner [Accessed 15 November 2025].

Developer Android. 2025. RecyclerView.Adapter, 15 May 2025. [Online]. Available at: https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter [Accessed 15 November 2025].

Developer Android. 2025. Add a floating action button, 30 October 2025. [Online]. Available at: https://developer.android.com/develop/ui/views/components/floating-action-button [Accessed 15 November 2025].

Developer Android. 2025. Better performance through threading, 3 January 2024. [Online]. Available at: https://developer.android.com/topic/performance/threads [Accessed 15 November 2025].

Developer Android. 2025. Kotlin coroutines on Android, 6 July 2024. [Online]. Available at: https://developer.android.com/kotlin/coroutines [Accessed 15 November 2025].

Firebase. 2025. Firebase Authentication, 20 October 2025. [Online]. Available at: https://firebase.google.com/docs/auth [Accessed 15 November 2025].

Firebase. 2025. Get Started with Firebase Authentication on Android, 14 November 2025. [Online]. Available at: https://firebase.google.com/docs/auth/android/start [Accessed 15 November 2025].

Firebase. 2025. Cloud Firestore, 14 November 2025. [Online]. Available at: https://firebase.google.com/docs/firestore [Accessed 15 November 2025].

Client authentication. 2025. 14 November 2025. [Online]. Available at: https://developers.google.com/android/guides/client-auth [Accessed 15 November 2025].
 */