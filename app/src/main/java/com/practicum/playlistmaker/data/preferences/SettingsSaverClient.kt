package com.practicum.playlistmaker.data.preferences

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.res.Configuration
import android.util.Log
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.data.Saver

class SettingsSaverClient : Saver<Boolean> {
    override fun getFromMemory(): Boolean? {
        val app = App.getInstance() ?: return null
        val sharedPrefs : SharedPreferences = app.getSharedPreferences(
            app.USER_SETTINGS_PREFERENCES, MODE_PRIVATE)
        val systemIsDarkMode = app.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        val darkTheme = sharedPrefs.getBoolean(app.DARK_THEME_KEY, systemIsDarkMode) // нельзя вернуть null по умолчанию, поэтому
                                                                                               // определение systemIsDarkMode будет дублироваться
        return darkTheme
    }

    override fun writeIntoMemory(t: Boolean?) {
        if (t != null) {
            val app = App.getInstance() ?: return
            val sharedPrefs : SharedPreferences = app.getSharedPreferences(
                app.USER_SETTINGS_PREFERENCES, MODE_PRIVATE)
            sharedPrefs.edit()
                .putBoolean(app.DARK_THEME_KEY, t)
                .apply()
        }
    }
}
