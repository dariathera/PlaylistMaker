package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.GetHistoryInteractor
import com.practicum.playlistmaker.domain.entities.Track
import com.practicum.playlistmaker.domain.repository.SearchHistorySaverRepository

class GetHistoryInteractorImpl(private val repository: SearchHistorySaverRepository) : GetHistoryInteractor {
    override fun getFromMemory(): ArrayDeque<Track> {
        return repository.getFromMemory()
    }

    override fun save(track : Track) {
        repository.save(track)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}
