package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.entities.Track

interface SearchHistoryRepository {
    fun getFromMemory(): ArrayDeque<Track>
    fun save(track : Track)
    fun clearHistory()
}