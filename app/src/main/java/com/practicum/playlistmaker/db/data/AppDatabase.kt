package com.practicum.playlistmaker.db.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.search.domain.entities.Track
import com.practicum.playlistmaker.library.domain.entities.PlaylistWithNoTracks

@Database(
    version = 9,
    entities = [
        Track::class,
        PlaylistWithNoTracks::class,
        FavoriteTrack::class,
        PlaylistTrackCrossRef::class
               ],
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getTrackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun getFavoriteDao(): FavoriteDao
    abstract fun getPlaylistTrackCrossRefDao(): PlaylistTrackCrossRefDao

}