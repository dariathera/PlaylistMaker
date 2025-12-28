package com.practicum.playlistmaker.data.preferences

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.data.Saver

class StringSearchHistorySaverClient : Saver<String> {

    override fun getFromMemory(): String? {
        val app = App.getInstance() ?: return null
        val sharedPrefs : SharedPreferences = app.getSharedPreferences(
            app.USER_SETTINGS_PREFERENCES, MODE_PRIVATE)
        val json = sharedPrefs.getString(
            app.SEARCH_HISTORY_KEY, null)
        return json
    }

    override fun writeIntoMemory(t: String?) {
        val app = App.getInstance() ?: return
        val sharedPrefs : SharedPreferences = app.getSharedPreferences(
            app.USER_SETTINGS_PREFERENCES, MODE_PRIVATE)
        sharedPrefs.edit().putString(app.SEARCH_HISTORY_KEY, t ?: "").apply()
    }

}