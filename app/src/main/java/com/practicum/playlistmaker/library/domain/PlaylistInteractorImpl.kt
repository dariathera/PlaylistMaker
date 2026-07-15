package com.practicum.playlistmaker.library.domain

import com.practicum.playlistmaker.library.domain.entities.Playlist
import com.practicum.playlistmaker.library.domain.entities.PlaylistGeneralInformation
import com.practicum.playlistmaker.search.domain.entities.Track

class PlaylistInteractorImpl(private val repository : PlaylistApi) : PlaylistInteractor {
    override suspend fun addNewPlaylist(playlist: Playlist) : Long {
        return repository.addNewPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist?) {
        repository.updatePlaylist(playlist)
    }

    override suspend fun getAllPlaylistsGeneralInfo(): List<PlaylistGeneralInformation> {
        return repository.getAllPlaylistsGeneralInfo()
    }

    override suspend fun getTracksIdListByPlaylistId(id: Long): MutableList<Track> {
        return repository.getTracksIdListByPlaylistId(id)
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        return repository.getPlaylistById(id)
    }

    override suspend fun getPlaylistNameByPlaylistId(id: Long): String? {
        return repository.getPlaylistNameByPlaylistId(id)
    }

}