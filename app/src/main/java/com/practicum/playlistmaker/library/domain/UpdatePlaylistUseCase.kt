package com.practicum.playlistmaker.library.domain

import android.net.Uri

interface UpdatePlaylistUseCase {
    suspend fun update(
        playlistId: Long,
        name: String,
        description: String,
        uri: Uri?
    )
}