package com.practicum.playlistmaker.library.ui.activity

import android.net.Uri

data class EditPlaylistLoadedData(
    val name: String = "",
    val description: String = "",
    val coverUri: Uri? = null
)