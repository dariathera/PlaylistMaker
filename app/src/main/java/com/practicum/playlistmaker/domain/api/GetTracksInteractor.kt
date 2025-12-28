package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.entities.GetMusicResponse


interface GetTracksInteractor {
    fun searchMusic(expression: String, consumer: GetMusicConsumer)

    interface GetMusicConsumer {
        fun consume(gotMusicResponse: GetMusicResponse)
    }
}