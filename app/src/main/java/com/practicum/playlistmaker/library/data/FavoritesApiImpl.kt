package com.practicum.playlistmaker.library.data

import com.practicum.playlistmaker.db.data.AppDatabase
import com.practicum.playlistmaker.library.domain.FavoritesApi
import com.practicum.playlistmaker.search.domain.entities.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesApiImpl(private val appDatabase: AppDatabase) : FavoritesApi {

    override suspend fun addNewFavorite(track: Track) {
        appDatabase.getTrackDao().insertNewTrack(track)
    }

    override suspend fun deleteFavorite(trackId: Long) {
        appDatabase.getTrackDao().deleteTrackById(trackId)
    }

    override suspend fun getAllFavorites(): Flow<List<Track>> = flow {
        emit( appDatabase.getTrackDao().getTracks())
    }
}