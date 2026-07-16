package com.practicum.playlistmaker.db.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.practicum.playlistmaker.library.domain.entities.Playlist
import com.practicum.playlistmaker.library.domain.entities.PlaylistWithNoTracks

@Dao
interface PlaylistDao {

    @Transaction
    @Query("SELECT * FROM playlist_table WHERE id = :playlistId")
    suspend fun getPlaylistWithTracks(playlistId: Long): Playlist?

    @Insert(entity = PlaylistWithNoTracks::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistWithNoTracks): Long

    @Update
    suspend fun updatePlaylist(playlist: PlaylistWithNoTracks)

    @Query("SELECT * FROM playlist_table WHERE id = :id")
    suspend fun getPlaylistById(id: Long): Playlist?

    @Query("SELECT name FROM playlist_table WHERE id = :id")
    suspend fun getPlaylistNameByPlaylistId(id: Long): String?

    @Query("SELECT * FROM playlist_table ORDER BY id DESC")
    suspend fun getAllPlaylists(): List<Playlist>

    @Query("DELETE FROM playlist_table")
    suspend fun clearAll()

}