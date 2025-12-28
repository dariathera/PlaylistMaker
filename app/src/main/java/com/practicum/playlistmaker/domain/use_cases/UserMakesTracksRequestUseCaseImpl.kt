package com.practicum.playlistmaker.domain.use_cases

import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.api.GetTracksInteractor
import com.practicum.playlistmaker.domain.api.UserMakesTracksRequestUseCase

class UserMakesTracksRequestUseCaseImpl : UserMakesTracksRequestUseCase {
    private val getMusicInteractor = Creator.provideGetTracksInteractor()

    override fun makeRequest(requestText: String, consumer : GetTracksInteractor.GetMusicConsumer) {
        getMusicInteractor.searchMusic( requestText.trim(), consumer)
    }
}

