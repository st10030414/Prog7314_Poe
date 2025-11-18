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

    private val vm: AuthViewModel by viewModels()

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

        signUpBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame, SignUpFragment())
                .addToBackStack(null)
                .commit()
        }

        buttonLogin.setOnClickListener {
            val email = editEmail.text?.toString().orEmpty()
            val pass = editPassword.text?.toString().orEmpty()
            if (email.isBlank() || pass.isBlank()) {
                Snackbar.make(view, "Email and password required.", Snackbar.LENGTH_SHORT).show()
            } else {
                vm.signIn(email, pass)
            }
        }

        offlineBtn.setOnClickListener {
            AppSession.isOffline = true
            (requireActivity() as MainActivity).setDrawerLocked(true)

            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame, OfflineHomeFragment())
                .commit()
        }

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

        applyFontSizes()
        return view
    }

    override fun onResume() {
        super.onResume()
        applyFontSizes()
    }

    private fun applyFontSizes() {
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "title"))
        prompt.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
        signUpBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
        editEmail.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
        editPassword.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
        buttonLogin.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
    }
}
