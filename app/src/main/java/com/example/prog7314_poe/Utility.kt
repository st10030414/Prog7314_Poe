package com.example.prog7314_poe

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.prog7314_poe.R

object Utility {

    /**
     * Returns font size in SP, based on type ("title" or "body") and user preference.
     */
    fun getFontDimen(context: Context, type: String): Float {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val fontChoice = prefs.getString("font_size", "medium") ?: "medium"

        val resId = when (fontChoice) {
            "small" -> if (type == "title") R.dimen.title_small else R.dimen.font_small
            "medium" -> if (type == "title") R.dimen.title_medium else R.dimen.font_medium
            "large" -> if (type == "title") R.dimen.title_large else R.dimen.font_large
            else -> if (type == "title") R.dimen.title_medium else R.dimen.font_medium
        }

        // Convert pixels to SP for proper scaling
        val px = context.resources.getDimension(resId)
        return px / context.resources.displayMetrics.scaledDensity
    }

    /**
     * Creates a spinner adapter that applies dynamic font sizes to each item.
     */
    fun getSpinnerAdapter(context: Context, arrayResId: Int): ArrayAdapter<String> {
        val array = context.resources.getStringArray(arrayResId)
        return object : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, array) {

            override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getView(position, convertView, parent) as TextView
                view.textSize = getFontDimen(context, "body") // Already in SP
                return view
            }

            override fun getDropDownView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.textSize = getFontDimen(context, "body") // Already in SP
                return view
            }
        }.also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
    }
}
