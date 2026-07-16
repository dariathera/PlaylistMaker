package com.practicum.playlistmaker.library.domain

import com.practicum.playlistmaker.search.domain.entities.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(private val repository : FavoritesApi) : FavoritesInteractor {
    override suspend fun addNewFavorite(track: Track) {
        repository.addNewFavorite(track)
    }

    override suspend fun deleteFavorite(track: Track) {
        repository.deleteFavorite(track.trackId)
    }

    override suspend fun getAllFavorites(): Flow<List<Track>> {
        return repository.getAllFavorites()
    }
}