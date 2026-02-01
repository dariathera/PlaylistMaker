package com.practicum.playlistmaker.search.domain

interface UserMakesTracksRequestUseCase {
    fun makeRequest(requestText: String, consumer : GetTracksInteractor.GetMusicConsumer)
}