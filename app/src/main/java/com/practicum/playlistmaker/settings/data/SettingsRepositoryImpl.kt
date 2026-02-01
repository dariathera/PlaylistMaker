package com.practicum.playlistmaker.settings.data

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.util.Saver
import com.practicum.playlistmaker.settings.domain.SettingsRepository

class SettingsRepositoryImpl(
    private val saverClient : Saver<Boolean>
) : SettingsRepository {
    private fun getSystemIsDarkMode() : Boolean {
        val app = App.Companion.getInstance() ?: return false
        val systemIsDarkMode = app.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        return systemIsDarkMode
    }

    override fun switchColorTheme(darkTheme: Boolean) {
        val darkThemeEnabled = darkTheme
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        saverClient.writeIntoMemory(darkTheme)
    }

    override fun getColorTheme() : Boolean {
        val darkTheme = saverClient.getFromMemory() ?: getSystemIsDarkMode()
        return darkTheme
    }
}