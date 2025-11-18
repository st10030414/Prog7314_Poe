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

    private var noteId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noteId = arguments?.getInt("note_id")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_note, container, false)

        title = view.findViewById(R.id.text_title)
        editor = view.findViewById(R.id.text_content)
        tagSpinner = view.findViewById(R.id.tag_selection)
        backBtn = view.findViewById(R.id.button_back)

        loadNote()

        val saveAndBack = {
            val contentHtml = editor.html ?: ""
            saveNote(title.text.toString(), contentHtml, tagSpinner.selectedItem.toString())
            requireActivity().supportFragmentManager.popBackStack()
        }

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
}
