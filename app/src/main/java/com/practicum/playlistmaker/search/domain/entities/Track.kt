package com.practicum.playlistmaker.search.domain.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "track_table",
    indices = [Index(value = ["trackId"], unique = true)]
)
data class Track(
    val trackName: String,
    val artistName: String,
    val trackTime: Int,
    val artworkUrl100: String,
    val trackId: Long,
    val album: String?,
    val year: Int?,
    val genre: String?,
    val country: String?,
    val previewUrl: String?
): Parcelable {
    @PrimaryKey(autoGenerate = true)
    var dbId: Long = 0
    @Ignore
    var isFavorite: Boolean = false
    fun getHighArtworkUrl() : String {
        return artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
    }
}