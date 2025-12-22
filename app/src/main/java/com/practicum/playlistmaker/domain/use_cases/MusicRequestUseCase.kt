package com.practicum.playlistmaker.domain.use_cases

import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.api.GetMusicInteractor

class MusicRequestUseCase {
    val getMusicInteractor = Creator.provideGetMusicInteractor()

    fun makeRequest(requestText: String, consumer : GetMusicInteractor.GetMusicConsumer) {
        getMusicInteractor.searchMusic( requestText.trim(), consumer)
    }
}

