package com.practicum.playlistmaker.library.domain

import android.net.Uri

interface CreatePlaylistUseCase {
    suspend fun create(
        name: String,
        description: String,
        uri: Uri?
    )
}