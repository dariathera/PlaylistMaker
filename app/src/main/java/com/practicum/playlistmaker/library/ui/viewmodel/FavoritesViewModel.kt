package com.practicum.playlistmaker.library.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.FavoritesInteractor
import com.practicum.playlistmaker.library.ui.activity.FavoritesState
import com.practicum.playlistmaker.search.domain.entities.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesInteractor : FavoritesInteractor
) : ViewModel() {
    private val stateLiveData = MutableLiveData<FavoritesState>(
        FavoritesState.Loading)
    fun observeState(): LiveData<FavoritesState> = stateLiveData

    fun updateState() {
        val favoriteTracks: MutableList<Track> = mutableListOf<Track>()
        viewModelScope.launch(Dispatchers.IO) {
            favoriteTracks.addAll(favoritesInteractor.getAllFavorites().firstOrNull() ?: emptyList())
            if (favoriteTracks.isEmpty()) {
                stateLiveData.postValue(FavoritesState.NoFavoritesTracks)
            } else {
                stateLiveData.postValue(FavoritesState.FavoritesTracks(favoriteTracks))
            }
        }
    }
}