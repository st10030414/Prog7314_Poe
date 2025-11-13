package com.example.prog7314_poe.Vault

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VaultViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = VaultRepository(app)

    val notes: StateFlow<List<VaultNoteUi>> =
        repo.observeNotes()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun add(title: String, content: String, tags: String?) {
        viewModelScope.launch { repo.add(title, content, tags) }
    }

    fun update(id: Int, title: String, content: String, tags: String?) {
        viewModelScope.launch { repo.update(id, title, content, tags) }
    }

    fun delete(id: Int) {
        viewModelScope.launch { repo.delete(id) }
    }
}
