package com.practicum.playlistmaker.library.data

import android.util.Log
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.db.data.AppDatabase
import com.practicum.playlistmaker.db.data.FavoriteDao
import com.practicum.playlistmaker.db.data.PlaylistDao
import com.practicum.playlistmaker.db.data.PlaylistTrackCrossRef
import com.practicum.playlistmaker.db.data.PlaylistTrackCrossRefDao
import com.practicum.playlistmaker.db.data.TrackDao
import com.practicum.playlistmaker.library.domain.PlaylistApi
import com.practicum.playlistmaker.library.domain.entities.Playlist
import com.practicum.playlistmaker.library.domain.entities.PlaylistGeneralInformation

class PlaylistApiImpl(private val appDatabase: AppDatabase) : PlaylistApi {

    private val playlistDao: PlaylistDao = appDatabase.playlistDao()
    private val crossRefDao: PlaylistTrackCrossRefDao = appDatabase.getPlaylistTrackCrossRefDao()
    private val trackDao: TrackDao = appDatabase.getTrackDao()
    private val favoriteDao: FavoriteDao = appDatabase.getFavoriteDao()

    override suspend fun addNewPlaylist(playlist: Playlist): Long {
        return playlistDao.insertPlaylist(playlist.playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist?) {
        if (playlist == null) return
        // 1. Обновляем данные плейлиста
        playlistDao.updatePlaylist(playlist.playlist)
        // 2. Перезаписываем связи с треками
        val playlistId = playlist.playlist.id
        val oldCrossRef = crossRefDao.getCrossRefsForPlaylist(playlistId)
        // Удаляем старые связи
        crossRefDao.deleteCrossRefsForPlaylist(playlistId)
        // Добавляем новые
        playlist.trackList.forEach { track ->
            val index = findIndexByTrackId(track.trackId, oldCrossRef)
            if (index == null) {
                crossRefDao.insertCrossRef(
                    PlaylistTrackCrossRef(playlistId, track.trackId)
                )
            } else {
                crossRefDao.insertCrossRef(
                    oldCrossRef[index]
                )
            }
            trackDao.insertNewTrack(track)
        }
    }

    private fun findIndexByTrackId(trackId: Long, list: List<PlaylistTrackCrossRef>): Int? {
        val index = list.indexOfFirst { it.trackId == trackId }
        return if (index == -1) null else index
    }

    override suspend fun getTracksIdListByPlaylistId(id: Long): List<Long> {
        return crossRefDao.getTrackIdsForPlaylist(id)
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        return playlistDao.getPlaylistById(id)
    }

    override suspend fun getSortedPlaylistById(id: Long): Playlist? {
        // 1. Загружаем плейлист (автоматически загружаются треки, но порядок не гарантирован)
        val playlist = playlistDao.getPlaylistById(id)

        // 2. Получаем отсортированные ID треков
        val sortedIds = crossRefDao.getSortedTrackIdsForPlaylist(id)

        // 3. Если треков нет, возвращаем плейлист с пустым списком
        if (sortedIds.isEmpty()) {
            playlist?.trackList = mutableListOf()
            return playlist
        }

        // 4. Загружаем все треки по ID в Map
        val tracksMap = trackDao.getTracksByIds(sortedIds).associateBy { it.trackId }

        // 5. Строим отсортированный список
        val sortedTracks = sortedIds.mapNotNull { tracksMap[it] }

        // 6. Заменяем список в плейлисте
        playlist?.trackList = sortedTracks.toMutableList()

        return playlist
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

    override suspend fun deleteTrackFromPlaylist(trackId: Long, playlistId: Long) {
        crossRefDao.deleteCrossRef(
            playlistId=playlistId,
            trackId=trackId
        )
        if (!(crossRefDao.isTrackInAnyPlaylist(trackId) ||
                    favoriteDao.isTrackFavorite(trackId))) {
            trackDao.deleteTrackById(trackId)
        }
    }

    override suspend fun deletePlaylist(id: Long) {
        val playlist = getPlaylistById(id)
        if (playlist != null && playlist.trackList.isEmpty()) {
            playlistDao.deletePlaylistWithNoTracks(playlist.playlist)
        } else if (playlist == null) {
            Log.e(App.Companion.ERROR_LOG_TAG, "В PlaylistApiImpl.deletePlaylist передан некорректный id")
        } else {
            Log.e(App.Companion.ERROR_LOG_TAG, "В PlaylistApiImpl.deletePlaylist передан плейлист, содержащий треки.\n" +
                    "Все треки должны быть удалены из плейлиста до вызова PlaylistApiImpl.deletePlaylist")
        }
    }

}
