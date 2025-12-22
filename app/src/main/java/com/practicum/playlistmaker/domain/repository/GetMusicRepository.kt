package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.entities.GetMusicResponse

interface GetMusicRepository {
    fun getMusic(expression: String) : GetMusicResponse
}