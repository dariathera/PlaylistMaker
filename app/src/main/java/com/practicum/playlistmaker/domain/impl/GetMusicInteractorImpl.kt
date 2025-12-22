package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.GetMusicInteractor
import com.practicum.playlistmaker.domain.repository.GetMusicRepository
import java.util.concurrent.Executors


class GetMusicInteractorImpl(private val repository: GetMusicRepository) : GetMusicInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchMusic(expression: String, consumer: GetMusicInteractor.GetMusicConsumer) {
        executor.execute {
            consumer.consume(repository.getMusic(expression))
        }
    }
}