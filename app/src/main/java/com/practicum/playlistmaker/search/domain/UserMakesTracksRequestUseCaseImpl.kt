package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.util.Creator

class UserMakesTracksRequestUseCaseImpl() : UserMakesTracksRequestUseCase {
    private val getMusicInteractor = Creator.provideGetTracksInteractor()

    override fun makeRequest(requestText: String, consumer : GetTracksInteractor.GetMusicConsumer) {
        getMusicInteractor.searchMusic( requestText.trim(), consumer)
    }
}