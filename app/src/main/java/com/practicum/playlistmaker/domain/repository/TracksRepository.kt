package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.entities.GetMusicResponse

interface TracksRepository {
    fun getMusic(expression: String) : GetMusicResponse
}