package com.example.edicoding.util

import android.content.Context
import android.content.SharedPreferences

class ThemePreferences(context: Context) {

    companion object {
        private const val PREFS_NAME = "theme_prefs"
        private const val KEY_IS_DARK_MODE = "is_dark_mode"
        private const val KEY_IS_DAILY_REMINDER_ACTIVE = "is_daily_reminder_active"
    }

    private val preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Fungsi untuk menyimpan status dark mode
    fun setDarkMode(isDarkMode: Boolean) {
        preferences.edit().putBoolean(KEY_IS_DARK_MODE, isDarkMode).apply()
    }

    // Fungsi untuk mendapatkan status dark mode
    fun isDarkMode(): Boolean {
        return preferences.getBoolean(KEY_IS_DARK_MODE, false)
    }

    fun setDailyReminderActive(isActive: Boolean) {
        preferences.edit().putBoolean(KEY_IS_DAILY_REMINDER_ACTIVE, isActive).apply()
    }

    fun isDailyReminderActive(): Boolean {
        return preferences.getBoolean(KEY_IS_DAILY_REMINDER_ACTIVE, false)
    }
}
