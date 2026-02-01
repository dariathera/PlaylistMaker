package com.practicum.playlistmaker.search.domain

import android.content.Context
import com.practicum.playlistmaker.util.Creator

class UserMakesTracksRequestUseCaseImpl(context : Context) : UserMakesTracksRequestUseCase {
    private val getMusicInteractor = Creator.provideGetTracksInteractor(context)

    override fun makeRequest(requestText: String, consumer : GetTracksInteractor.GetMusicConsumer) {
        getMusicInteractor.searchMusic( requestText.trim(), consumer)
    }
}