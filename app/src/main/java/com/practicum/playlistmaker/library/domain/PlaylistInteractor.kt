package com.practicum.playlistmaker.library.domain

import com.practicum.playlistmaker.library.domain.entities.Playlist
import com.practicum.playlistmaker.library.domain.entities.PlaylistGeneralInformation
import com.practicum.playlistmaker.search.domain.entities.Track

interface PlaylistInteractor {
    suspend fun addNewPlaylist(playlist: Playlist) : Long
    suspend fun updatePlaylist(playlist: Playlist?)
    suspend fun getAllPlaylistsGeneralInfo() :  List<PlaylistGeneralInformation>
    suspend fun getTracksIdListByPlaylistId(id: Long): List<Long>
    suspend fun getPlaylistById(id: Long): Playlist?
    suspend fun getPlaylistNameByPlaylistId(id: Long): String?
}