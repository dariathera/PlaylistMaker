package com.practicum.playlistmaker

import android.app.Application
import android.content.res.Configuration
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    val USER_SETTINGS_PREFERENCES = "user_settings_file"
    val DARK_THEME_KEY = "key_for_dark_them"
    val SEARCH_HISTORY_KEY = "key_for_search_history"
    var darkTheme = false
        private set

    override fun onCreate() {
        super.onCreate()
        setInstance(this)
        Log.d("Playlist Maker Debug", "App.onCreate() вызван, INSTANCE = $INSTANCE")
        val systemIsDarkMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        val sharedPrefs = getSharedPreferences(USER_SETTINGS_PREFERENCES, MODE_PRIVATE)
        switchTheme(sharedPrefs.getBoolean(DARK_THEME_KEY, systemIsDarkMode))
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        val sharedPrefs = getSharedPreferences(USER_SETTINGS_PREFERENCES, MODE_PRIVATE)
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        sharedPrefs.edit()
            .putBoolean(DARK_THEME_KEY, darkTheme)
            .apply()
    }

    companion object {
        @Volatile
        private var INSTANCE: App? = null
        fun getInstance(): App? = INSTANCE
        fun setInstance(app: App) {
            INSTANCE = app
        }
    }
}