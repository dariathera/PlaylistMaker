package com.practicum.playlistmaker.player.service

import kotlinx.coroutines.flow.Flow

interface MusicServiceApi {
    fun showNotifications()
    fun hideNotifications()
    fun playbackControl()
    val isPlaying: Flow<Boolean>
    val timeText: Flow<String>
    val isPlaybackCompleted: Flow<Boolean>
}