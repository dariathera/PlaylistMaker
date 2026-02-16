package com.practicum.playlistmaker.search_history.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.domain.entities.Track
import com.practicum.playlistmaker.util.Saver
import com.practicum.playlistmaker.search_history.domain.SearchHistoryRepository

class SearchHistoryRepositoryImpl (
    private val saverClient : Saver<String>,
    private val gson: Gson
) : SearchHistoryRepository
{
    private val maxQuantity = 10

    override fun getFromMemory(): ArrayDeque<Track> {
        val json = saverClient.getFromMemory() ?: return ArrayDeque<Track>()
        val type = object : TypeToken<ArrayDeque<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    private fun writeIntoMemory(tracks: ArrayDeque<Track>) {
        val json = gson.toJson(tracks)
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
    }

    override fun clearHistory() {
        writeIntoMemory(ArrayDeque<Track>())
    }

}