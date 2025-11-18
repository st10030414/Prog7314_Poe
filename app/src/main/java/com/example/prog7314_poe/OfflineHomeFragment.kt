package com.example.prog7314_poe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prog7314_poe.Offline.OfflineNotesAdapt
import com.example.prog7314_poe.Offline.OfflineDatabase
import com.example.prog7314_poe.MainActivity
import com.example.prog7314_poe.LoginFragment
import com.example.prog7314_poe.R
import com.example.prog7314_poe.Session.AppSession
import com.google.android.material.floatingactionbutton.FloatingActionButton

class OfflineHomeFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: OfflineNotesAdapt
    private lateinit var goOnlineBtn: Button
    private lateinit var addNoteBtn: FloatingActionButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_offline_home, container, false)

        (requireActivity() as MainActivity).setDrawerLocked(true)

        recycler = view.findViewById(R.id.offline_notes_view)
        goOnlineBtn = view.findViewById(R.id.go_online_btn)
        addNoteBtn = view.findViewById(R.id.offline_add_note)

        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = OfflineNotesAdapt(emptyList()) { note -> openNote(note.id) }
        recycler.adapter = adapter

        recycler.adapter = adapter

        loadOfflineNotes()

        goOnlineBtn.setOnClickListener {
            AppSession.isOffline = false
            (requireActivity() as MainActivity).setDrawerLocked(false)

            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame, LoginFragment())
                .commit()
        }

        addNoteBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame, OfflineNoteFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun loadOfflineNotes() {
        val db = OfflineDatabase.getInstance(requireContext())
        db.offlineNoteDao().getAllNotes().observe(viewLifecycleOwner) { notes ->
            adapter.updateNotes(notes)
        }
    }

    private fun openNote(noteId: Int) {
        val fragment = OfflineNoteFragment().apply {
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
