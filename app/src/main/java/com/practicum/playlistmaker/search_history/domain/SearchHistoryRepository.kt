package com.practicum.playlistmaker.search_history.domain

import com.practicum.playlistmaker.search.domain.entities.Track

interface SearchHistoryRepository {
    suspend fun getFromMemory(): ArrayDeque<Track>
    suspend fun save(track : Track)
    fun clearHistory()
}