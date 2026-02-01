package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.entities.Track
import com.practicum.playlistmaker.util.Resource

interface GetTracksInteractor {
    fun searchMusic(expression: String, consumer: GetMusicConsumer)

    interface GetMusicConsumer {
        fun consume(resource: Resource<MutableList<Track>>)
    }
}