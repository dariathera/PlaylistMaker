package com.practicum.playlistmaker.data.repository

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.data.Saver
import com.practicum.playlistmaker.domain.repository.SettingsSaverRepository

class SettingsSaverRepositoryImpl(
    private val saverClient : Saver<Boolean>
) : SettingsSaverRepository {
    private fun getSystemIsDarkMode() : Boolean {
        val app = App.getInstance() ?: return false
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