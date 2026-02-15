package com.practicum.playlistmaker

import android.app.Application
import android.content.Context
import android.util.Log
import com.practicum.playlistmaker.util.Creator

class
App : Application() {
    val USER_SETTINGS_PREFERENCES = "user_settings_file"
    val DARK_THEME_KEY = "key_for_dark_them"
    val SEARCH_HISTORY_KEY = "key_for_search_history"
    val settingsSaver = Creator.provideSettingsInteractor()

    override fun onCreate() {
        super.onCreate()
        setInstance(this)
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

        fun getContext(): Context {
            val app = getInstance()
            if (app != null) {
                return app.applicationContext
            } else {
                Log.e(ERROR_LOG_TAG, "Не получилось вернуть applicationContext")
                throw Exception("No applicationContext")
            }
        }

        const val DEBUG_LOG_TAG = "Playlist Maker Debug"
        const val ERROR_LOG_TAG = "Playlist Maker Error"
    }
}