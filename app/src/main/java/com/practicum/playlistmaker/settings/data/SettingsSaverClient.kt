package com.practicum.playlistmaker.settings.data

import android.content.SharedPreferences
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.util.Saver

class SettingsSaverClient(
    private val sharedPrefs : SharedPreferences?,
    private val key: String?,
    private val app: App?
) : Saver<Boolean> {

    override fun getFromMemory(): Boolean? {
        if (app == null) {
            return null
        } else {
            return sharedPrefs?.getBoolean(key, app.getSystemDarkMode())
        }
    }

    override fun writeIntoMemory(t: Boolean?) {
        if (t != null) {
            sharedPrefs?.edit()
                ?.putBoolean(key, t)
                ?.apply()
        }
    }
}