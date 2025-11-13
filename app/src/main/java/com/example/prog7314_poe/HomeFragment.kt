package com.example.prog7314_poe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prog7314_poe.Notes.AppDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {

    private lateinit var openNotesBtn: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotesAdapt

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.notes_view)
        adapter = NotesAdapt(emptyList()) { note ->
            openNoteForEditing(note.id)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        openNotesBtn = view.findViewById(R.id.open_notes_Btn)
        openNotesBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame, NoteFragment())
                .addToBackStack(null)
                .commit()
        }

        observeNotes()

        return view
    }

    private fun observeNotes() {
        val db = AppDatabase.getDatabase(requireContext())
        db.noteDao().getAllNotes().observe(viewLifecycleOwner) { notes ->
            adapter.updateNotes(notes)
        }
    }

    private fun openNoteForEditing(noteId: Int) {
        val fragment = NoteFragment().apply {
            arguments = Bundle().apply {
                putInt("note_id", noteId)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.content_frame, fragment)
            .addToBackStack(null)
            .commit()
    }
}
