package com.example.prog7314_poe

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.EditText
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.prog7314_poe.databinding.FragmentVaultBinding
import com.example.prog7314_poe.security.BiometricGate
import com.example.prog7314_poe.security.SecurePrefs
import com.example.prog7314_poe.ui.adapter.VaultNotesAdapter
import com.example.prog7314_poe.Vault.VaultNoteUi
import com.example.prog7314_poe.Vault.VaultViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest

class VaultFragment : Fragment() {

    private var _binding: FragmentVaultBinding? = null
    private val binding get() = _binding!!

    private val vm: VaultViewModel by viewModels()
    private lateinit var adapter: VaultNotesAdapter
    //(Developer Android, 2025).
    private var unlocked = false
    //(Developer Android, 2025).

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVaultBinding.inflate(inflater, container, false)
        return binding.root
    }
    //(Developer Android, 2025).
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.root.post {
            gateAccess()
        }
        //(Developer Android, 2025).

        adapter = VaultNotesAdapter(
            onClick = { note -> showEditDialog(note) },
            onLongPress = { note, anchor -> showItemMenu(note, anchor) }
        )
        //(Developer Android, 2025).
        binding.recyclerVault.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerVault.adapter = adapter
        //(Developer Android, 2025).
        binding.fabAddSecure.setOnClickListener {
            if (!unlocked) {
                Snackbar.make(view, "Unlock vault first", Snackbar.LENGTH_SHORT).show()
            } else {
                showAddDialog()
            }
        }
        //(Developer Android, 2025).
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vm.notes.collectLatest { items ->
                binding.recyclerVault.isVisible = unlocked
                if (unlocked) adapter.submitList(items)
            }
        }
        //(Developer Android, 2025).
    }

    private fun gateAccess() {
        val host = requireActivity() as MainActivity
        val gate = BiometricGate(host)
        if (gate.canAuth(requireContext())) {
            gate.prompt(object : BiometricGate.Callback {
                override fun onAuthenticated() {
                    unlocked = true
                    Snackbar.make(binding.root, "Vault unlocked", Snackbar.LENGTH_SHORT).show()
                }
                override fun onFailed(errMsg: String) {
                    promptPin()
                }
                override fun onUnavailable() {
                    promptPin()
                }
            })
        } else {
            promptPin()
        }
        //(Developer Android, 2025).
    }

    private fun promptPin() {
        val current = SecurePrefs.getPin(requireContext())
        if (current == null) {
            val input = EditText(requireContext()).apply {
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
                hint = "Set a 4-6 digit PIN"
            }
            AlertDialog.Builder(requireContext())
                .setTitle("Set Vault PIN")
                .setView(input)
                .setPositiveButton("Save") { _, _ ->
                    val pin = input.text?.toString()?.trim().orEmpty()
                    if (pin.length in 4..6) {
                        SecurePrefs.setPin(requireContext(), pin)
                        unlocked = true
                        Snackbar.make(binding.root, "PIN set. Vault unlocked", Snackbar.LENGTH_LONG).show()
                    } else {
                        Snackbar.make(binding.root, "PIN must be 4-6 digits", Snackbar.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        } else {
            val input = EditText(requireContext()).apply {
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
                hint = "Enter PIN"
            }
            AlertDialog.Builder(requireContext())
                .setTitle("Vault PIN")
                .setView(input)
                .setPositiveButton("Unlock") { _, _ ->
                    val pin = input.text?.toString()?.trim().orEmpty()
                    if (pin == SecurePrefs.getPin(requireContext())) {
                        unlocked = true
                        Snackbar.make(binding.root, "Vault unlocked", Snackbar.LENGTH_SHORT).show()
                    } else {
                        Snackbar.make(binding.root, "Incorrect PIN", Snackbar.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun showAddDialog() {
        val v = layoutInflater.inflate(R.layout.dialog_edit_vault, null)
        val title = v.findViewById<EditText>(R.id.input_title)
        val content = v.findViewById<EditText>(R.id.input_content)
        val tags = v.findViewById<EditText>(R.id.input_tags)

        AlertDialog.Builder(requireContext())
            .setTitle("New secure note")
            .setView(v)
            .setPositiveButton("Save") { _, _ ->
                vm.add(title.text?.toString().orEmpty(), content.text?.toString().orEmpty(), tags.text?.toString())
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditDialog(note: VaultNoteUi) {
        val v = layoutInflater.inflate(R.layout.dialog_edit_vault, null)
        val title = v.findViewById<EditText>(R.id.input_title)
        val content = v.findViewById<EditText>(R.id.input_content)
        val tags = v.findViewById<EditText>(R.id.input_tags)
        title.setText(note.title)
        content.setText(note.content)
        tags.setText(note.tags ?: "")

        AlertDialog.Builder(requireContext())
            .setTitle("Edit secure note")
            .setView(v)
            .setPositiveButton("Update") { _, _ ->
                vm.update(
                    note.id,
                    title.text?.toString().orEmpty(),
                    content.text?.toString().orEmpty(),
                    tags.text?.toString()
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showItemMenu(note: VaultNoteUi, anchor: View) {
        PopupMenu(requireContext(), anchor).apply {
            menu.add("Delete")
            setOnMenuItemClickListener {
                if (it.title == "Delete") vm.delete(note.id)
                true
            }
        }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
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