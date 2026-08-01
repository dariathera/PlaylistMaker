package com.practicum.playlistmaker.db.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistTrackCrossRefDao {

    @Insert(entity = PlaylistTrackCrossRef::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(crossRef: PlaylistTrackCrossRef)

    @Query("""
    INSERT OR IGNORE INTO playlist_track_cross_ref (playlistId, trackId, timestamp) 
    VALUES (:playlistId, :trackId, :timestamp)
""")
    suspend fun insertCrossRef(playlistId: Long, trackId: Long, timestamp: Long)

    @Query("DELETE FROM playlist_track_cross_ref WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun deleteCrossRef(playlistId: Long, trackId: Long)

    @Query("DELETE FROM playlist_track_cross_ref WHERE playlistId = :playlistId")
    suspend fun deleteCrossRefsForPlaylist(playlistId: Long)

    @Query("""
    SELECT * 
    FROM playlist_track_cross_ref 
    WHERE playlistId = :playlistId 
    """)
    suspend fun getCrossRefsForPlaylist(playlistId: Long): List<PlaylistTrackCrossRef>

    @Query("SELECT trackId FROM playlist_track_cross_ref WHERE playlistId = :playlistId")
    suspend fun getTrackIdsForPlaylist(playlistId: Long): List<Long>

    @Query("""
    SELECT trackId 
    FROM playlist_track_cross_ref 
    WHERE playlistId = :playlistId 
    ORDER BY timestamp DESC
    """)
    suspend fun getSortedTrackIdsForPlaylist(playlistId: Long): List<Long>

    @Query("SELECT EXISTS(SELECT 1 FROM playlist_track_cross_ref WHERE trackId = :trackId)")
    suspend fun isTrackInAnyPlaylist(trackId: Long): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM playlist_track_cross_ref WHERE trackId = :trackId AND playlistId = :playlistId)")
    suspend fun isTrackInPlaylist(trackId: Long, playlistId: Long): Boolean

    @Query("DELETE FROM playlist_track_cross_ref")
    suspend fun clearAll()
}