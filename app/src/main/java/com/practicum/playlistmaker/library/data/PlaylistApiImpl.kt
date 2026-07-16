package com.practicum.playlistmaker.library.data

import com.practicum.playlistmaker.db.data.AppDatabase
import com.practicum.playlistmaker.db.data.PlaylistDao
import com.practicum.playlistmaker.db.data.PlaylistTrackCrossRef
import com.practicum.playlistmaker.db.data.PlaylistTrackCrossRefDao
import com.practicum.playlistmaker.db.data.TrackDao
import com.practicum.playlistmaker.library.domain.PlaylistApi
import com.practicum.playlistmaker.library.domain.entities.Playlist
import com.practicum.playlistmaker.library.domain.entities.PlaylistGeneralInformation
import com.practicum.playlistmaker.search.domain.entities.Track
import kotlin.Long
import kotlin.String

class PlaylistApiImpl(private val appDatabase: AppDatabase) : PlaylistApi {

    private val playlistDao: PlaylistDao = appDatabase.playlistDao()
    private val crossRefDao: PlaylistTrackCrossRefDao = appDatabase.getPlaylistTrackCrossRefDao()
    private val trackDao: TrackDao = appDatabase.getTrackDao()

    override suspend fun addNewPlaylist(playlist: Playlist): Long {
        // 1. Вставляем данные плейлиста (без треков)
        val playlistId = playlistDao.insertPlaylist(playlist.playlist)
        // 2. Сохраняем связи с треками, если они есть
        playlist.trackList.forEach { track ->
            crossRefDao.insertCrossRef(
                PlaylistTrackCrossRef(playlistId, track.trackId)
            )
            trackDao.insertNewTrack(track)
        }
        return playlistId
    }

    override suspend fun updatePlaylist(playlist: Playlist?) {
        if (playlist == null) return
        // 1. Обновляем данные плейлиста
        playlistDao.updatePlaylist(playlist.playlist)
        // 2. Перезаписываем связи с треками
        val playlistId = playlist.playlist.id
        // Удаляем старые связи
        crossRefDao.deleteCrossRefsForPlaylist(playlistId)
        // Добавляем новые
        playlist.trackList.forEach { track ->
            crossRefDao.insertCrossRef(
                PlaylistTrackCrossRef(playlistId, track.trackId)
            )
            trackDao.insertNewTrack(track)
        }
    }

    override suspend fun getTracksIdListByPlaylistId(id: Long): List<Long> {
        return crossRefDao.getTrackIdsForPlaylist(id)
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        return playlistDao.getPlaylistById(id)
    }

    override suspend fun getPlaylistNameByPlaylistId(id: Long): String? {
        return playlistDao.getPlaylistNameByPlaylistId(id)
    }

    override suspend fun getAllPlaylistsGeneralInfo() =
        appDatabase.playlistDao().getAllPlaylists()
            .map {
                PlaylistGeneralInformation(
                    it.playlist.id,
                    it.playlist.name,
                    it.playlist.coverFileName,
                    it.trackList.size
                )
            }
}
