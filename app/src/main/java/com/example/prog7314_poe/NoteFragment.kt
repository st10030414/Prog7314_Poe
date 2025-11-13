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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noteId = arguments?.getInt("note_id") // get note id if passed
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_note, container, false)

        // Bind views
        title = view.findViewById(R.id.text_title)
        editor = view.findViewById(R.id.text_content)
        tagSpinner = view.findViewById(R.id.tag_selection)
        backBtn = view.findViewById(R.id.button_back)
        // Format buttons
        view.findViewById<Button>(R.id.btn_bold).setOnClickListener { editor.setBold() }
        view.findViewById<Button>(R.id.btn_italic).setOnClickListener { editor.setItalic() }
        view.findViewById<Button>(R.id.btn_underline).setOnClickListener { editor.setUnderline() }
        view.findViewById<Button>(R.id.btn_bullet).setOnClickListener { editor.setBullets() }


        applyFontSizes()
        loadNoteIfEditing() // Load existing note if editing

        val saveAndBack = {
            val contentHtml = editor.html ?: ""
            saveNote(title.text.toString(), contentHtml, tagSpinner.selectedItem.toString())
            requireActivity().supportFragmentManager.popBackStack()
        }

        backBtn.setOnClickListener { saveAndBack() }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { saveAndBack() }

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

    private fun applyFontSizes() {
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
        editor.setEditorFontSize(Utility.getFontDimen(requireContext(), "body").toInt())
        tagSpinner.adapter = createSpinnerAdapter(tagSpinner, R.array.tag_prompts)
    }

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
}
