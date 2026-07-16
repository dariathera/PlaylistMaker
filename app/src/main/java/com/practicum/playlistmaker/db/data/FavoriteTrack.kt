package com.practicum.playlistmaker.db.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_table",
    indices = [Index(value = ["trackId"], unique = true)]
)
data class FavoriteTrack(
    val trackId: Long
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}