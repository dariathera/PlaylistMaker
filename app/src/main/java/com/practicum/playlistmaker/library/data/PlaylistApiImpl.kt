package com.practicum.playlistmaker.library.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.db.data.AppDatabase
import com.practicum.playlistmaker.library.domain.FavoritesApi
import com.practicum.playlistmaker.library.domain.PlaylistApi
import com.practicum.playlistmaker.library.domain.entities.Playlist
import com.practicum.playlistmaker.library.domain.entities.PlaylistGeneralInformation
import com.practicum.playlistmaker.search.domain.entities.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.Long
import kotlin.String

class PlaylistApiImpl(private val appDatabase: AppDatabase) : PlaylistApi {
    override suspend fun addNewPlaylist(playlist: Playlist) : Long {
        return appDatabase.playlistDao().insertPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist?) {
        if (playlist != null) {
            appDatabase.playlistDao().updatePlaylist(playlist)
        }
    }

    override suspend fun getAllPlaylistsGeneralInfo() =
        appDatabase.playlistDao().getAllPlaylists()
            .map {
                PlaylistGeneralInformation(
                    it.id,
                    it.name,
                    it.coverFileName,
                    it.trackList.size
                )
            }
    /*
    override suspend fun getAllPlaylistsGeneralInfo() :  List<PlaylistGeneralInformation> {
        val playlists: List<Playlist> = appDatabase.playlistDao().getAllPlaylists()
        val generalInfo: MutableList<PlaylistGeneralInformation> = mutableListOf()
        for (playlist in playlists) {
            generalInfo.add(PlaylistGeneralInformation(
                playlist.id,
                playlist.name,
                playlist.coverFileName,
                playlist.trackList.size
            ))
        }
        return generalInfo.toList()
    }

     */

    override suspend fun getTracksIdListByPlaylistId(id: Long): MutableList<Track> {
        val json = appDatabase.playlistDao().getTracksJsonByPlaylistId(id)
        val mutableTrackList: MutableList<Track> = Gson().fromJson(json, object : TypeToken<MutableList<Track>>() {}.type)
        return mutableTrackList
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        return appDatabase.playlistDao().getPlaylistById(id)
    }

    override suspend fun getPlaylistNameByPlaylistId(id: Long): String? {
        return appDatabase.playlistDao().getPlaylistNameByPlaylistId(id)
    }

}
