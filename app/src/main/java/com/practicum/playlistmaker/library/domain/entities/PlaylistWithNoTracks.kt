package com.practicum.playlistmaker.library.domain.entities

import android.net.Uri
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(
    tableName = "playlist_table"
)
data class PlaylistWithNoTracks(
    val name: String,
    val description: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var coverFileName: String? = null
    @Ignore
    var uri: Uri? = null

    constructor(
        _name: String,
        _description: String,
        _coverFileName: String?
    ) : this(_name, _description) {
        coverFileName = _coverFileName
    }

}