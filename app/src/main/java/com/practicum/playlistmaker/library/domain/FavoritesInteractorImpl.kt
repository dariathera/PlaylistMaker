package com.practicum.playlistmaker.library.domain

import com.practicum.playlistmaker.search.domain.entities.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesInteractorImpl(private val repository : FavoritesApi) : FavoritesInteractor {
    override suspend fun addNewFavorite(track: Track) {
        repository.addNewFavorite(track)
    }

    override suspend fun deleteFavorite(track: Track) {
        repository.deleteFavorite(track.trackId)
        track.dbId = 0
    }

    override suspend fun getAllFavorites(): Flow<List<Track>> {
        return repository.getAllFavorites().map { list ->
            list.sortedByDescending { it.dbId }
        }
    }
}