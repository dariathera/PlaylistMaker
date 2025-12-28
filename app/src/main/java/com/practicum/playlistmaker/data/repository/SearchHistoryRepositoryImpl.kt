package com.practicum.playlistmaker.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.Saver
import com.practicum.playlistmaker.domain.entities.Track
import com.practicum.playlistmaker.domain.repository.SearchHistoryRepository

class SearchHistoryRepositoryImpl (
    private val saverClient : Saver<String>
    ) : SearchHistoryRepository
{
    private val maxQuantity = 10

    override fun getFromMemory(): ArrayDeque<Track> {
        val json = saverClient.getFromMemory() ?: return ArrayDeque<Track>()
        val type = object : TypeToken<ArrayDeque<Track>>() {}.type
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
    }

    override fun clearHistory() {
        writeIntoMemory(ArrayDeque<Track>())
    }

}