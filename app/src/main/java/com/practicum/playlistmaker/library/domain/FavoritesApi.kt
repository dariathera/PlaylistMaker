package com.practicum.playlistmaker.library.domain

import com.practicum.playlistmaker.search.domain.entities.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesApi {
    suspend fun addNewFavorite(track: Track)
    suspend fun deleteFavorite(trackId: Long)
    suspend fun getAllFavorites(): Flow<List<Track>>
}