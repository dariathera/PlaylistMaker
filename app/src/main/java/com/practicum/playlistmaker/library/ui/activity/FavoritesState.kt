package com.practicum.playlistmaker.library.ui.activity

import com.practicum.playlistmaker.search.domain.entities.Track

sealed interface  FavoritesState {
    object NoFavoritesTracks : FavoritesState
    data class FavoritesTracks(val tracks: MutableList<Track>) : FavoritesState
}