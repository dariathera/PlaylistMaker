package com.practicum.playlistmaker.library.domain.entities

import androidx.room.Entity
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

    constructor(
        _name: String,
        _description: String,
        _coverFileName: String?
    ) : this(_name, _description) {
        coverFileName = _coverFileName
    }

}