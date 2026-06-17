package com.practicum.playlistmaker.library.domain

import com.practicum.playlistmaker.search.domain.entities.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    suspend fun addNewFavorite(track: Track)
    suspend fun deleteFavorite(track: Track)
    suspend fun getAllFavorites(): Flow<List<Track>>
}