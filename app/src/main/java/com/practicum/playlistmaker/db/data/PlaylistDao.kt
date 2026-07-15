package com.practicum.playlistmaker.db.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.practicum.playlistmaker.library.domain.entities.Playlist
import com.practicum.playlistmaker.library.domain.entities.PlaylistGeneralInformation
import com.practicum.playlistmaker.search.domain.entities.Track

@Dao
interface PlaylistDao {
    @Insert(entity = Playlist::class, onConflict = OnConflictStrategy.NONE)
    suspend fun insertPlaylist(playlist: Playlist) : Long

    @Update(entity = Playlist::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePlaylist(playlist: Playlist)

    @Query("DELETE FROM playlist_table WHERE id = :id")
    suspend fun deletePlaylistById(id: Long)

    @Query("SELECT * FROM playlist_table ORDER BY id DESC")
    suspend fun getAllPlaylists(): List<Playlist>

    @Query("SELECT * FROM playlist_table WHERE id = :id")
    suspend fun getPlaylistById(id: Long): Playlist?

    @Query("SELECT trackList FROM playlist_table WHERE id = :id")
    suspend fun getTracksJsonByPlaylistId(id: Long): String?

    @Query("SELECT name FROM playlist_table WHERE id = :id")
    suspend fun getPlaylistNameByPlaylistId(id: Long): String?

}