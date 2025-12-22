package com.practicum.playlistmaker.data.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.Saver
import com.practicum.playlistmaker.domain.entities.Track
import com.practicum.playlistmaker.domain.repository.SearchHistorySaverRepository

class SearchHistorySaverRepositoryImpl (
    private val saverClient : Saver<String>
    ) : SearchHistorySaverRepository
{
    private val maxQuantity = 10

    override fun getFromMemory(): ArrayDeque<Track> {
        val json = saverClient.getFromMemory() ?: return ArrayDeque<Track>()
        val type = object : TypeToken<ArrayDeque<Track>>() {}.type
        Log.d("Playlist Maker Debug", "Получены $json")
        return Gson().fromJson(json, type)
    }

    private fun writeIntoMemory(tracks: ArrayDeque<Track>) {
        val json = Gson().toJson(tracks)
        saverClient.writeIntoMemory(json)
    }

    override fun save(track : Track) {
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

    override fun clearHistory() {
        writeIntoMemory(ArrayDeque<Track>())
    }

}