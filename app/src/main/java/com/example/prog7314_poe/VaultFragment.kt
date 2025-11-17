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

    private var unlocked = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Use ViewBinding for convenience (enable in Gradle if not already).
        _binding = FragmentVaultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.root.post {
            gateAccess()
        }


        adapter = VaultNotesAdapter(
            onClick = { note -> showEditDialog(note) },
            onLongPress = { note, anchor -> showItemMenu(note, anchor) }
        )

        binding.recyclerVault.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerVault.adapter = adapter

        binding.fabAddSecure.setOnClickListener {
            if (!unlocked) {
                Snackbar.make(view, "Unlock vault first", Snackbar.LENGTH_SHORT).show()
            } else {
                showAddDialog()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vm.notes.collectLatest { items ->
                binding.recyclerVault.isVisible = unlocked
                if (unlocked) adapter.submitList(items)
            }
        }
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
                    // fallback to PIN
                    promptPin()
                }
                override fun onUnavailable() {
                    promptPin()
                }
            })
        } else {
            promptPin()
        }
    }

    private fun promptPin() {
        val current = SecurePrefs.getPin(requireContext())
        if (current == null) {
            // set new PIN
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
