package com.practicum.playlistmaker.settings.data

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.util.Saver
import com.practicum.playlistmaker.settings.domain.SettingsRepository

class SettingsRepositoryImpl(
    private val saverClient : Saver<Boolean>,
    private val app: App?
) : SettingsRepository {

    override fun switchColorTheme(darkTheme: Boolean) {
        val darkThemeEnabled = darkTheme
        saverClient.writeIntoMemory(darkTheme)
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    override fun getColorTheme() : Boolean {
        if (app != null) {
            val darkTheme = saverClient.getFromMemory() ?: app.getSystemDarkMode()
            return darkTheme
        }
        Log.e(App.Companion.ERROR_LOG_TAG, "Невозможная ситуация: в SettingsRepositoryImpl переданo app = null.")
        return false
    }
}