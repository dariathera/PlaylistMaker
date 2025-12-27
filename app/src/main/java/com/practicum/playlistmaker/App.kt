package com.practicum.playlistmaker

import android.app.Application
import android.content.res.Configuration
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.creator.Creator

class
App : Application() {
    val USER_SETTINGS_PREFERENCES = "user_settings_file"
    val DARK_THEME_KEY = "key_for_dark_them"
    val SEARCH_HISTORY_KEY = "key_for_search_history"
    val settingsSaver = Creator.provideGetSettingsInteractor()

    override fun onCreate() {
        super.onCreate()
        setInstance(this)
        Log.d("Playlist Maker Debug", "App.onCreate() вызван, INSTANCE = $INSTANCE")
        val darkTheme = settingsSaver.getColorTheme()
        settingsSaver.switchColorTheme(darkTheme)
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