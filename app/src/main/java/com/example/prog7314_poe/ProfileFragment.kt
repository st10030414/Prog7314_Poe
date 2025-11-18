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
    //(Developer Android, 2025).
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
        //(Developer Android, 2025).
        applyFontSizes()

        val email = FirebaseAuth.getInstance().currentUser?.email
        textEmail.text = email ?: "Unknown"

        buttonChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        return view
    }
    //(Developer Android, 2025).
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
    //(Developer Android, 2025).


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
    //(Developer Android, 2025).


    override fun onResume() {
        super.onResume()
        applyFontSizes()
    }
    //(Developer Android, 2025).
    private fun applyFontSizes() {
        titleEmail.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "title"))
        titlePassword.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "title"))
        textEmail.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
        textPassword.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
        buttonChangePassword.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
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