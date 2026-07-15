package com.practicum.playlistmaker.library.domain

import android.net.Uri
import com.practicum.playlistmaker.library.domain.entities.Playlist

interface CreatePlaylistUseCase {
    suspend fun create(
        name: String,
        description: String,
        uri: Uri?
    )


}