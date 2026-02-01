package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.entities.Track
import com.practicum.playlistmaker.util.Resource

interface TracksRepository {
    fun getMusic(expression: String) : Resource<MutableList<Track>>
}