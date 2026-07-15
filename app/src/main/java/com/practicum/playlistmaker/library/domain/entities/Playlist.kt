package com.practicum.playlistmaker.library.domain.entities

import android.net.Uri
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.practicum.playlistmaker.search.domain.entities.Track

@Entity(
    tableName = "playlist_table"
)
data class Playlist(
    val name: String,
    val description: String,
)
{
    @PrimaryKey(autoGenerate = true)
    var id : Long = 0
    var coverFileName: String? = null
    var trackList: MutableList<Track> = mutableListOf<Track>()
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