package com.practicum.playlistmaker.library.ui.activity

import android.net.Uri

data class PlaylistInfoState (
    val name: String = "",
    val description: String = "",
    val totalDuration: Int = 0,
    val numberOfTracks: Int = 0,
    val coverUri: Uri? = null
)