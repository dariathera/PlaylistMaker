package com.practicum.playlistmaker.search_history.data

import android.content.SharedPreferences
import com.practicum.playlistmaker.util.Saver

class StringSearchHistorySaverClient(
    private val sharedPrefs : SharedPreferences?,
    private val key: String?
) : Saver<String> {

    override fun getFromMemory(): String? {
        return sharedPrefs?.getString(key, null)
    }

    override fun writeIntoMemory(t: String?) {
        sharedPrefs?.edit()?.putString(key, t ?: "")?.apply()
    }
}