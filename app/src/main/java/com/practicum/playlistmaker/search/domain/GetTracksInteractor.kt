package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.entities.Track
import com.practicum.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface GetTracksInteractor {
    fun searchMusic(expression: String) : Flow<Resource<MutableList<Track>>>
}
