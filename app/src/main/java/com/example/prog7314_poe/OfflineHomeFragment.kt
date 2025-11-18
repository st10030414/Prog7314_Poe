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
    //(Developer Android, 2025).

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
        //(Developer Android, 2025).
        loadOfflineNotes()

        goOnlineBtn.setOnClickListener {
            AppSession.isOffline = false
            (requireActivity() as MainActivity).setDrawerLocked(false)

            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame, LoginFragment())
                .commit()
        }
        //(Developer Android, 2025).
        addNoteBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame, OfflineNoteFragment())
                .addToBackStack(null)
                .commit()
        }
        //(Developer Android, 2025).
        return view
    }

    private fun loadOfflineNotes() {
        val db = OfflineDatabase.getInstance(requireContext())
        db.offlineNoteDao().getAllNotes().observe(viewLifecycleOwner) { notes ->
            adapter.updateNotes(notes)
        }
    }
    //(Developer Android, 2025).
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