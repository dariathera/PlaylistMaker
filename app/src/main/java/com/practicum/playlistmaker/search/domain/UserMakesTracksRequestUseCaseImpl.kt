package com.practicum.playlistmaker.search.domain

import org.koin.core.component.KoinComponent

class UserMakesTracksRequestUseCaseImpl(private val getMusicInteractor: GetTracksInteractor) :
    UserMakesTracksRequestUseCase,
    KoinComponent
{
    override fun makeRequest(requestText: String, consumer : GetTracksInteractor.GetMusicConsumer) {
        getMusicInteractor.searchMusic( requestText.trim(), consumer)
    }
}

