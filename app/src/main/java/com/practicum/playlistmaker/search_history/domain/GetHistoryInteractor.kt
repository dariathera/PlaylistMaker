package com.practicum.playlistmaker.search_history.domain

import com.practicum.playlistmaker.search.domain.entities.Track

interface GetHistoryInteractor {
    suspend fun getFromMemory(): ArrayDeque<Track>
    suspend fun save(track : Track)
    fun clearHistory()
}