package com.example.prog7314_poe

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {

    private lateinit var titleUsername: TextView
    private lateinit var textUsername: TextView
    private lateinit var titleEmail: TextView
    private lateinit var textEmail: TextView
    private lateinit var titlePassword: TextView
    private lateinit var textPassword: TextView
    private lateinit var buttonChangePassword: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Bind views
        titleUsername = view.findViewById(R.id.title_username)
        textUsername = view.findViewById(R.id.text_username)
        titleEmail = view.findViewById(R.id.title_email)
        textEmail = view.findViewById(R.id.text_email)
        titlePassword = view.findViewById(R.id.title_password)
        textPassword = view.findViewById(R.id.text_password)
        buttonChangePassword = view.findViewById(R.id.button_change_password)

        // Apply font sizes
        applyFontSizes()

        return view
    }

    override fun onResume() {
        super.onResume()
        // Re-apply font sizes in case settings changed
        applyFontSizes()
    }

    private fun applyFontSizes() {
        // Titles
        titleUsername.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "title"))
        titleEmail.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "title"))
        titlePassword.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "title"))

        // Body text and button
        textUsername.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
        textEmail.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
        textPassword.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
        buttonChangePassword.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
    }
}
