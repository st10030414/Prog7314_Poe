package com.example.prog7314_poe

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.prog7314_poe.Session.AppSession
import com.example.prog7314_poe.auth.AuthUiState
import com.example.prog7314_poe.auth.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest

class LoginFragment : Fragment() {

    private lateinit var title: TextView
    private lateinit var prompt: TextView
    private lateinit var signUpBtn: TextView
    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var progress: ProgressBar
    //(Developer Android, 2025).
    private val vm: AuthViewModel by viewModels()
    //(Developer Android, 2025).
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val offlineBtn = view.findViewById<TextView>(R.id.offlineBtn)
        title = view.findViewById(R.id.title)
        prompt = view.findViewById(R.id.prompt)
        signUpBtn = view.findViewById(R.id.signUpBtn)
        editEmail = view.findViewById(R.id.edit_email)
        editPassword = view.findViewById(R.id.edit_password)
        buttonLogin = view.findViewById(R.id.button_login)
        progress = view.findViewById(R.id.progress)
        //(Developer Android, 2025).
        signUpBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame, SignUpFragment())
                .addToBackStack(null)
                .commit()
        }
        //(Developer Android, 2025).
        buttonLogin.setOnClickListener {
            val email = editEmail.text?.toString().orEmpty()
            val pass = editPassword.text?.toString().orEmpty()
            if (email.isBlank() || pass.isBlank()) {
                Snackbar.make(view, "Email and password required.", Snackbar.LENGTH_SHORT).show()
            } else {
                vm.signIn(email, pass)
            }
        }
        //(Developer Android, 2025).
        offlineBtn.setOnClickListener {
            AppSession.isOffline = true
            (requireActivity() as MainActivity).setDrawerLocked(true)

            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame, OfflineHomeFragment())
                .commit()
        }
        //(Developer Android, 2025).
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vm.state.collectLatest { state ->
                when (state) {
                    is AuthUiState.Idle -> progress.isVisible = false
                    is AuthUiState.Loading -> progress.isVisible = true
                    is AuthUiState.Success -> {
                        progress.isVisible = false
                        (requireActivity() as MainActivity).setDrawerLocked(false)
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.content_frame, HomeFragment())
                            .commit()
                    }
                    is AuthUiState.Error -> {
                        progress.isVisible = false
                        Snackbar.make(view, state.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
        //(Developer Android, 2025).

        applyFontSizes()
        return view
    }

    override fun onResume() {
        super.onResume()
        applyFontSizes()
    }
    //(Developer Android, 2025).
    private fun applyFontSizes() {
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "title"))
        prompt.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
        signUpBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
        editEmail.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
        editPassword.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
        buttonLogin.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
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