package com.practicum.playlistmaker.db.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.search.domain.entities.Track

@Database(
    version = 1,
    entities = [Track::class],
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getTrackDao(): TrackDao

}