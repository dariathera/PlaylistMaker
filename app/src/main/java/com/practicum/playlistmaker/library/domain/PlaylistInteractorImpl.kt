package com.practicum.playlistmaker.library.domain

import com.practicum.playlistmaker.library.domain.entities.Playlist
import com.practicum.playlistmaker.library.domain.entities.PlaylistGeneralInformation

class PlaylistInteractorImpl(
    private val repository : PlaylistApi,
    private val privateStorage: PrivateStorageApi
) : PlaylistInteractor {
    override suspend fun addNewPlaylist(playlist: Playlist) : Long {
        return repository.addNewPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist?) {
        repository.updatePlaylist(playlist)
    }

    override suspend fun getAllPlaylistsGeneralInfo(): List<PlaylistGeneralInformation> {
        return repository.getAllPlaylistsGeneralInfo()
    }

    override suspend fun getTracksIdListByPlaylistId(id: Long): List<Long> {
        return repository.getTracksIdListByPlaylistId(id)
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        return repository.getSortedPlaylistById(id)
    }

    override suspend fun getPlaylistNameByPlaylistId(id: Long): String? {
        return repository.getPlaylistNameByPlaylistId(id)
    }

    override suspend fun deleteTrackFromPlaylist(trackId: Long, playlistId: Long) {
        repository.deleteTrackFromPlaylist(
            trackId=trackId,
            playlistId=playlistId
        )
    }

    override suspend fun deletePlaylistById(id: Long) {
        val playlist = getPlaylistById(id)
        if (playlist == null) return
        for (track in playlist.trackList) {
            deleteTrackFromPlaylist(
                trackId=track.trackId,
                playlistId=id
            )
        }
        privateStorage.deleteImage(
            playlist.playlist.coverFileName
        )
        repository.deletePlaylist(id)
    }
}