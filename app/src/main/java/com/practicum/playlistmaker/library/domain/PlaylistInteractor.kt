package com.practicum.playlistmaker.library.domain

import com.practicum.playlistmaker.library.domain.entities.Playlist
import com.practicum.playlistmaker.library.domain.entities.PlaylistGeneralInformation

interface PlaylistInteractor {
    suspend fun addNewPlaylist(playlist: Playlist) : Long
    suspend fun updatePlaylist(playlist: Playlist?)
    suspend fun getAllPlaylistsGeneralInfo() :  List<PlaylistGeneralInformation>
    suspend fun getTracksIdListByPlaylistId(id: Long): List<Long>
    suspend fun getPlaylistById(id: Long): Playlist?
    suspend fun getPlaylistNameByPlaylistId(id: Long): String?
    suspend fun deleteTrackFromPlaylist(trackId: Long, playlistId: Long)
    suspend fun deletePlaylistById(id: Long)

}