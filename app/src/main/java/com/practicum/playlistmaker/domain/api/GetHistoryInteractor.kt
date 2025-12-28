package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.entities.Track

interface GetHistoryInteractor {
    fun getFromMemory(): ArrayDeque<Track>
    fun save(track : Track)
    fun clearHistory()
}