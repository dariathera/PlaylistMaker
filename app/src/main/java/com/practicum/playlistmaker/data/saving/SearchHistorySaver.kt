package com.practicum.playlistmaker.data.saving

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.activities.SearchActivity
import com.practicum.playlistmaker.data.objects.Track

object SearchHistorySaver {
    private val maxQuantity = 10

    fun getFromMemory(): ArrayDeque<Track> {
        Log.d("Playlist Maker Debug", "Мы в методе getFromMemory")
        val app = App.getInstance() ?: return ArrayDeque<Track>()
        val sharedPrefs : SharedPreferences = app.getSharedPreferences(
            app.USER_SETTINGS_PREFERENCES, MODE_PRIVATE)
        val json = sharedPrefs.getString(
            app.SEARCH_HISTORY_KEY, null) ?: return ArrayDeque<Track>()
        val type = object : TypeToken<ArrayDeque<Track>>() {}.type
        Log.d("Playlist Maker Debug", "Получены $json")
        return Gson().fromJson(json, type)
    }

    private fun writeIntoMemory(tracks: ArrayDeque<Track>) {
        val app = App.getInstance() ?: return
        val sharedPrefs : SharedPreferences = app.getSharedPreferences(
            app.USER_SETTINGS_PREFERENCES, MODE_PRIVATE)
        val json = Gson().toJson(tracks)
        sharedPrefs.edit().putString(app.SEARCH_HISTORY_KEY, json).apply()
    }

    fun save(track : Track) {
        val savedTracks: ArrayDeque<Track> = getFromMemory()
        var indexToRemove: Int? = null

        for (index in 0 until savedTracks.size) {
            if (savedTracks[index].trackId == track.trackId) {
                indexToRemove = index
                break
            }
        }

        if (indexToRemove != null) {
            savedTracks.removeAt(indexToRemove)
        }

        if (savedTracks.size >= maxQuantity) {
            savedTracks.removeLast()
        }

        savedTracks.addFirst(track)
        writeIntoMemory(savedTracks)
        Log.d("Playlist Maker Debug", "Мы в методе save. Сохранены $savedTracks")
    }

    fun clearHistory() {
        writeIntoMemory(ArrayDeque<Track>())
    }

}