package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.TracksRepository
import java.util.concurrent.Executors

class GetTracksInteractorImpl(private val repository: TracksRepository) : GetTracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchMusic(expression: String, consumer: GetTracksInteractor.GetMusicConsumer) {
        executor.execute {
            consumer.consume(repository.getMusic(expression))
        }
    }
}