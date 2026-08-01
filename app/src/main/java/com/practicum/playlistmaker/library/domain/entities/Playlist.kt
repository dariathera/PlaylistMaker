package com.practicum.playlistmaker.library.domain.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.practicum.playlistmaker.db.data.PlaylistTrackCrossRef
import com.practicum.playlistmaker.search.domain.entities.Track

data class Playlist(
    @Embedded val playlist: PlaylistWithNoTracks,
    @Relation(
        parentColumn = "id",
        entityColumn = "trackId",
        associateBy = Junction(
            value = PlaylistTrackCrossRef::class,
            parentColumn = "playlistId",
            entityColumn = "trackId"
        )
    )
    var trackList: MutableList<Track> = mutableListOf<Track>()
)