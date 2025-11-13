package com.example.prog7314_poe.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.prog7314_poe.R
import com.example.prog7314_poe.Vault.VaultNoteUi

class VaultNotesAdapter(
    private val onClick: (VaultNoteUi) -> Unit,
    private val onLongPress: (VaultNoteUi, View) -> Unit
) : ListAdapter<VaultNoteUi, VaultNotesAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<VaultNoteUi>() {
        override fun areItemsTheSame(old: VaultNoteUi, new: VaultNoteUi) = old.id == new.id
        override fun areContentsTheSame(old: VaultNoteUi, new: VaultNoteUi) = old == new
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.tv_title)
        private val preview: TextView = itemView.findViewById(R.id.tv_preview)
        fun bind(item: VaultNoteUi) {
            title.text = item.title
            preview.text = item.content
            itemView.setOnClickListener { onClick(item) }
            itemView.setOnLongClickListener {
                onLongPress(item, it)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vault_note, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }
}
