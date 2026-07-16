package com.practicum.playlistmaker.library.data

import com.practicum.playlistmaker.db.data.AppDatabase
import com.practicum.playlistmaker.db.data.FavoriteTrack
import com.practicum.playlistmaker.library.domain.FavoritesApi
import com.practicum.playlistmaker.search.domain.entities.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class


FavoritesApiImpl(private val appDatabase: AppDatabase) : FavoritesApi {

    override suspend fun addNewFavorite(track: Track) {
        appDatabase.getTrackDao().insertNewTrack(track)
        val favorite = FavoriteTrack(track.trackId)
        appDatabase.getFavoriteDao().addToFavorites(favorite)
    }

    override suspend fun deleteFavorite(trackId: Long) {
        appDatabase.getFavoriteDao().deleteTrackById(trackId)
        if (!appDatabase.getPlaylistTrackCrossRefDao().isTrackInAnyPlaylist(trackId)) {
            appDatabase.getTrackDao().deleteTrackById(trackId)
        }
    }

    override suspend fun getAllFavorites(): Flow<List<Track>> = flow {
        emit( appDatabase.getFavoriteDao().getAllFavoritesOrdered())
    }
}
