package com.example.prog7314_poe

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.*

class SettingsFragment : Fragment() {

    private lateinit var darkModeLabel: TextView
    private lateinit var languageLabel: TextView
    private lateinit var fontSizeLabel: TextView
    private lateinit var sortOrderLabel: TextView
    private lateinit var fontSpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val prefs = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE)

        // Bind TextViews
        darkModeLabel = view.findViewById(R.id.dark_mode)
        languageLabel = view.findViewById(R.id.language)
        fontSizeLabel = view.findViewById(R.id.font_size)
        sortOrderLabel = view.findViewById(R.id.sort_order)
        fontSpinner = view.findViewById(R.id.spinner_font_size)

        // Apply font sizes on all labels
        applyFontSizes()

        // Dark Mode
        val switch = view.findViewById<SwitchMaterial>(R.id.switch_dark_mode)
        val isDark = prefs.getBoolean("dark_mode", false)
        switch.isChecked = isDark
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        switch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("dark_mode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        // Language
        val spinnerLanguage = view.findViewById<Spinner>(R.id.spinner_language)
        val languageCodes = listOf("en", "af", "zu")
        val savedLanguage = prefs.getString("app_language", "en") ?: "en"
        spinnerLanguage.setSelection(languageCodes.indexOf(savedLanguage), false)

        spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLang = languageCodes[position]
                val currentLang = prefs.getString("app_language", "en") ?: "en"
                if (selectedLang != currentLang) {
                    prefs.edit().putString("app_language", selectedLang).apply()
                    setLocale(selectedLang)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Font size
        val fontSizes = listOf("small", "medium", "large")
        val savedFont = prefs.getString("font_size", "medium") ?: "medium"
        fontSpinner.setSelection(fontSizes.indexOf(savedFont), false)

        fontSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedFont = fontSizes[position]
                val currentFont = prefs.getString("font_size", "medium") ?: "medium"
                if (selectedFont != currentFont) {
                    prefs.edit().putString("font_size", selectedFont).apply()
                    applyFontSizes()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        return view
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        requireActivity().resources.updateConfiguration(config, requireActivity().resources.displayMetrics)
        requireActivity().recreate()
    }

    private fun applyFontSizes() {
        darkModeLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
        languageLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
        fontSizeLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
        sortOrderLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getFontDimen(requireContext(), "body"))
    }

    override fun onResume() {
        super.onResume()
        applyFontSizes() // re-apply in case font settings changed elsewhere
    }
}
