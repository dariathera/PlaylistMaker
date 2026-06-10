package com.practicum.playlistmaker.search.domain

import kotlinx.coroutines.flow.Flow
import com.practicum.playlistmaker.search.domain.entities.Track
import com.practicum.playlistmaker.util.Resource


class GetTracksInteractorImpl(private val repository: TracksRepository) : GetTracksInteractor {

    override fun searchMusic(expression: String) : Flow<Resource<MutableList<Track>>> {
        return repository.getMusic(expression)
    }
}