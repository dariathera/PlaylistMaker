package com.practicum.playlistmaker.db.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.practicum.playlistmaker.library.domain.entities.Playlist
import com.practicum.playlistmaker.search.domain.entities.Track

@Database(
    version = 6,
    entities = [Track::class, Playlist::class],
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getTrackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao

}