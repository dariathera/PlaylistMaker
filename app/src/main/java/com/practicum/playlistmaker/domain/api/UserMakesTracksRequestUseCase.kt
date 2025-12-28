package com.practicum.playlistmaker.domain.api

interface UserMakesTracksRequestUseCase {
    fun makeRequest(requestText: String, consumer : GetTracksInteractor.GetMusicConsumer)
}