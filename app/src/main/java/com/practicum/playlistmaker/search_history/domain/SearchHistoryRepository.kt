package com.practicum.playlistmaker.search_history.domain

import com.practicum.playlistmaker.search.domain.entities.Track

interface SearchHistoryRepository {
    fun getFromMemory(): ArrayDeque<Track>
    fun save(track : Track)
    fun clearHistory()
}