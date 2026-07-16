package com.practicum.playlistmaker.db.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.search.domain.entities.Track

@Dao
interface TrackDao {
    @Insert(entity = Track::class, onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertNewTrack(track: Track)

    @Query("DELETE FROM track_table WHERE trackId = :id")
    suspend fun deleteTrackById(id: Long)

    @Query("SELECT * FROM track_table")
    suspend fun getTracks(): List<Track>

    @Query("SELECT trackId FROM track_table")
    suspend fun getTrackIds(): List<Long>

    @Query("SELECT * FROM track_table WHERE trackId = :trackId")
    suspend fun getTrackById(trackId: Long): Track?

    @Query("SELECT * FROM track_table WHERE trackId IN (:trackIds)")
    suspend fun getTracksByIds(trackIds: List<Long>): List<Track>
}