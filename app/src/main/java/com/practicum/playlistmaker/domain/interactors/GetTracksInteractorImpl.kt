package com.practicum.playlistmaker.domain.interactors

import com.practicum.playlistmaker.domain.api.GetTracksInteractor
import com.practicum.playlistmaker.domain.repository.TracksRepository
import java.util.concurrent.Executors


class GetTracksInteractorImpl(private val repository: TracksRepository) : GetTracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchMusic(expression: String, consumer: GetTracksInteractor.GetMusicConsumer) {
        executor.execute {
            consumer.consume(repository.getMusic(expression))
        }
    }
}