package com.example.prog7314_poe

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.example.prog7314_poe.Notes.AppDatabase
import com.example.prog7314_poe.Notes.Note
import jp.wasabeef.richeditor.RichEditor
import kotlinx.coroutines.*

class NoteFragment : Fragment() {

    private lateinit var title: EditText
    private lateinit var editor: RichEditor
    private lateinit var tagSpinner: Spinner
    private lateinit var backBtn: ImageButton
    private var noteId: Int? = null
    private val spinnerFontSp = 18f
    //(Developer Android, 2025).
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noteId = arguments?.getInt("note_id")
    }
    //(Developer Android, 2025).
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_note, container, false)

        title = view.findViewById(R.id.text_title)
        editor = view.findViewById(R.id.text_content)
        tagSpinner = view.findViewById(R.id.tag_selection)
        backBtn = view.findViewById(R.id.button_back)

        view.findViewById<Button>(R.id.btn_bold).setOnClickListener { editor.setBold() }
        view.findViewById<Button>(R.id.btn_italic).setOnClickListener { editor.setItalic() }
        view.findViewById<Button>(R.id.btn_underline).setOnClickListener { editor.setUnderline() }
        view.findViewById<Button>(R.id.btn_bullet).setOnClickListener { editor.setBullets() }
        //(Developer Android, 2025).

        applyFontSizes()
        loadNoteIfEditing()

        val saveAndBack = {
            val contentHtml = editor.html ?: ""
            saveNote(title.text.toString(), contentHtml, tagSpinner.selectedItem.toString())
            requireActivity().supportFragmentManager.popBackStack()
        }
        //(Developer Android, 2025).
        backBtn.setOnClickListener { saveAndBack() }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { saveAndBack() }
        //(Developer Android, 2025).
        return view
    }

    private fun loadNoteIfEditing() {
        noteId?.let { id ->
            val db = AppDatabase.getDatabase(requireContext())
            val noteDao = db.noteDao()

            CoroutineScope(Dispatchers.IO).launch {
                val note = noteDao.getNoteById(id)
                withContext(Dispatchers.Main) {
                    note?.let {
                        title.setText(it.title)
                        editor.html = it.content
                        val tagIndex = resources.getStringArray(R.array.tag_prompts).indexOf(it.tags)
                        if (tagIndex >= 0) tagSpinner.setSelection(tagIndex)
                    }
                }
            }
        }
    }
    //(Developer Android, 2025).

    private fun saveNote(titleText: String, contentHtml: String, tagText: String) {
        if (titleText.isBlank() && contentHtml.isBlank()) return

        val db = AppDatabase.getDatabase(requireContext())
        val noteDao = db.noteDao()

        CoroutineScope(Dispatchers.IO).launch {
            if (noteId != null) {
                val existing = noteDao.getNoteById(noteId!!)
                existing?.let {
                    val updatedNote = it.copy(title = titleText, content = contentHtml, tags = tagText)
                    noteDao.update(updatedNote)
                }
            } else {
                val newNote = Note(title = titleText, content = contentHtml, tags = tagText)
                noteDao.insert(newNote)
            }
        }
    }
    //(Developer Android, 2025).

    private fun applyFontSizes() {
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
        editor.setEditorFontSize(Utility.getFontDimen(requireContext(), "body").toInt())
        tagSpinner.adapter = createSpinnerAdapter(tagSpinner, R.array.tag_prompts)
    }
    //(Developer Android, 2025).
    private fun createSpinnerAdapter(spinner: Spinner, arrayResId: Int): ArrayAdapter<String> {
        val items = resources.getStringArray(arrayResId)
        return object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, items) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, spinnerFontSp)
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, spinnerFontSp)
                return view
            }
        }.also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
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