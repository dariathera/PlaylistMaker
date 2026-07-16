package com.practicum.playlistmaker.db.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.search.domain.entities.Track

@Dao
interface FavoriteDao {
    @Insert(entity = FavoriteTrack::class, onConflict = OnConflictStrategy.Companion.NONE)
    suspend fun addToFavorites(track: FavoriteTrack)

    @Query("DELETE FROM favorite_table WHERE trackId = :trackId")
    suspend fun deleteTrackById(trackId: Long)

    @Query("""
    SELECT track_table.* 
    FROM track_table 
    INNER JOIN favorite_table ON track_table.trackId = favorite_table.trackId 
    ORDER BY favorite_table.id DESC
    """)
    suspend fun getAllFavoritesOrdered(): List<Track>

    @Query("DELETE FROM favorite_table")
    suspend fun clearAll()
}