package com.offplanpro.crm.util

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {
    fun applyTheme(theme: String) {
        when (theme) {
            "ocean", "purple", "rose", "gold" ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    fun getTheme(prefs: SharedPreferences): String =
        prefs.getString("theme", "gold") ?: "gold"

    fun saveTheme(prefs: SharedPreferences, theme: String) =
        prefs.edit().putString("theme", theme).apply()
}
