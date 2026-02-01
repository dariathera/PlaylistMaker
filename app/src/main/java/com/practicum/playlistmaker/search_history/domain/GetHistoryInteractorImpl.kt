package com.practicum.playlistmaker.search_history.domain

import com.practicum.playlistmaker.search.domain.entities.Track

class GetHistoryInteractorImpl(private val repository: SearchHistoryRepository) :
    GetHistoryInteractor {
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