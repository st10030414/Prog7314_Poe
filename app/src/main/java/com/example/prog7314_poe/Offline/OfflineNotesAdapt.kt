package com.example.prog7314_poe.Offline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.prog7314_poe.R

class OfflineNotesAdapt(
    private var notes: List<OfflineNote>,
    private val onNoteClick: (OfflineNote) -> Unit
) : RecyclerView.Adapter<OfflineNotesAdapt.NoteViewHolder>() {

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.note_title)
        val content: TextView = itemView.findViewById(R.id.note_content)
        val tag: TextView = itemView.findViewById(R.id.note_tag)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]

        holder.title.text = note.title.ifEmpty { "(No title)" }
        holder.content.text = note.content
        holder.tag.text = note.tags

        holder.itemView.setOnClickListener {
            onNoteClick(note)
        }
    }

    override fun getItemCount(): Int = notes.size

    fun updateNotes(newNotes: List<OfflineNote>) {
        notes = newNotes
        notifyDataSetChanged()
    }
}
