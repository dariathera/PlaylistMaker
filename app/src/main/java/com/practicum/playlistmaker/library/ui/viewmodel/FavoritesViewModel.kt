package com.practicum.playlistmaker.library.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.library.ui.activity.FavoritesState

class FavoritesViewModel : ViewModel() {
    private val stateLiveData = MutableLiveData<FavoritesState>(
        updateState())
    fun observeState(): LiveData<FavoritesState> = stateLiveData

    private fun updateState() : FavoritesState {
        // логика получения треков из избранного +
        // определение состояния фрагмента
        return FavoritesState.NoFavoritesTracks
    }
}