package com.practicum.playlistmaker.library.domain.entities

import android.net.Uri
import androidx.room.Ignore

data class PlaylistGeneralInformation (
    val id: Long,
    val name: String,
    val coverFileName: String?,
    val quantity: Int
) {
    @Ignore
    var uri: Uri? = null
}

