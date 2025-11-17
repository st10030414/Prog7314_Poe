package com.example.prog7314_poe

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider


class ProfileFragment : Fragment() {

    private lateinit var titleEmail: TextView
    private lateinit var textEmail: TextView
    private lateinit var titlePassword: TextView
    private lateinit var textPassword: TextView
    private lateinit var buttonChangePassword: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        titleEmail = view.findViewById(R.id.title_email)
        textEmail = view.findViewById(R.id.text_email)
        titlePassword = view.findViewById(R.id.title_password)
        textPassword = view.findViewById(R.id.text_password)
        buttonChangePassword = view.findViewById(R.id.button_change_password)

        applyFontSizes()

        // Fetch the email from FirebaseAuth
        val email = FirebaseAuth.getInstance().currentUser?.email
        textEmail.text = email ?: "Unknown"

        // Set up the password change button
        buttonChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        return view
    }

    private fun showChangePasswordDialog() {
        val layout = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_change_password, null)

        val currentPass = layout.findViewById<EditText>(R.id.current_password)
        val newPass = layout.findViewById<EditText>(R.id.new_password)

        AlertDialog.Builder(requireContext())
            .setTitle("Change Password")
            .setView(layout)
            .setPositiveButton("Change") { _, _ ->
                val oldPassword = currentPass.text.toString()
                val newPassword = newPass.text.toString()

                if (oldPassword.isBlank() || newPassword.isBlank()) {
                    Toast.makeText(requireContext(), "All fields required.", Toast.LENGTH_SHORT).show()
                } else if (newPassword.length < 6) {
                    Toast.makeText(requireContext(), "New password too short.", Toast.LENGTH_SHORT).show()
                } else {
                    reauthenticateAndChange(oldPassword, newPassword)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun reauthenticateAndChange(oldPassword: String, newPassword: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email

        if (user == null || email == null) {
            Toast.makeText(requireContext(), "Not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        val credential = EmailAuthProvider.getCredential(email, oldPassword)

        user.reauthenticate(credential)
            .addOnSuccessListener {
                // Now safe to update password
                user.updatePassword(newPassword)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Password updated!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Update failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Reauthentication failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }


    override fun onResume() {
        super.onResume()
        applyFontSizes()
    }

    private fun applyFontSizes() {
        titleEmail.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "title"))
        titlePassword.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "title"))
        textEmail.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
        textPassword.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
        buttonChangePassword.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
    }
}
