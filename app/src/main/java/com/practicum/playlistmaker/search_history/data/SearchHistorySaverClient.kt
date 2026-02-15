package com.practicum.playlistmaker.search_history.data

import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.util.Saver

class StringSearchHistorySaverClient : Saver<String> {

    override fun getFromMemory(): String? {
        val app = App.Companion.getInstance() ?: return null
        val sharedPrefs : SharedPreferences = app.getSharedPreferences(
            app.USER_SETTINGS_PREFERENCES, Context.MODE_PRIVATE
        )
        val json = sharedPrefs.getString(
            app.SEARCH_HISTORY_KEY, null)
        return json
    }

    override fun writeIntoMemory(t: String?) {
        val app = App.Companion.getInstance() ?: return
        val sharedPrefs : SharedPreferences = app.getSharedPreferences(
            app.USER_SETTINGS_PREFERENCES, Context.MODE_PRIVATE
        )
        sharedPrefs.edit().putString(app.SEARCH_HISTORY_KEY, t ?: "").apply()
    }

}