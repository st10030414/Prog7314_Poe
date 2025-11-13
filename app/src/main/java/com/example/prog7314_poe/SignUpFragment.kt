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
import com.example.prog7314_poe.auth.AuthUiState
import com.example.prog7314_poe.auth.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest

class SignUpFragment : Fragment() {

    private lateinit var title: TextView
    private lateinit var prompt: TextView
    private lateinit var loginLink: TextView
    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var editConfirmPassword: EditText
    private lateinit var buttonSignUp: Button
    private lateinit var progress: ProgressBar

    private val vm: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        title = view.findViewById(R.id.title_signup)
        prompt = view.findViewById(R.id.prompt)
        loginLink = view.findViewById(R.id.signUpBtn) // “Login” link in this screen
        editEmail = view.findViewById(R.id.edit_email)
        editPassword = view.findViewById(R.id.edit_password)
        editConfirmPassword = view.findViewById(R.id.edit_confirm_password)
        buttonSignUp = view.findViewById(R.id.button_sign_up)
        progress = view.findViewById(R.id.progress)

        loginLink.setOnClickListener { parentFragmentManager.popBackStack() }

        buttonSignUp.setOnClickListener {
            val email = editEmail.text?.toString().orEmpty()
            val p1 = editPassword.text?.toString().orEmpty()
            val p2 = editConfirmPassword.text?.toString().orEmpty()
            when {
                email.isBlank() || p1.isBlank() || p2.isBlank() ->
                    Snackbar.make(view, "All fields are required.", Snackbar.LENGTH_SHORT).show()
                p1.length < 6 ->
                    Snackbar.make(view, "Password must be at least 6 characters.", Snackbar.LENGTH_SHORT).show()
                p1 != p2 ->
                    Snackbar.make(view, "Passwords do not match.", Snackbar.LENGTH_SHORT).show()
                else -> vm.signUp(email, p1)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vm.state.collectLatest { state ->
                when (state) {
                    is AuthUiState.Idle -> progress.isVisible = false
                    is AuthUiState.Loading -> progress.isVisible = true
                    is AuthUiState.Success -> {
                        progress.isVisible = false
                        (requireActivity() as MainActivity).setDrawerLocked(false)
                        parentFragmentManager.popBackStack(null, 0)
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
        loginLink.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
        editEmail.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
        editPassword.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
        editConfirmPassword.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
        buttonSignUp.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
    }
}
