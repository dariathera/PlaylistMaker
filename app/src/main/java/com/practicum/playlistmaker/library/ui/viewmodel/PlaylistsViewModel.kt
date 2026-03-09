package com.practicum.playlistmaker.library.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.library.ui.activity.PlaylistsState

class PlaylistsViewModel : ViewModel() {
    private val stateLiveData = MutableLiveData<PlaylistsState>(
        updateState())
    fun observeState(): LiveData<PlaylistsState> = stateLiveData

    private fun updateState() : PlaylistsState {
        // логика получения плейлистов пользователя +
        // определение состояния фрагмента
        return PlaylistsState.NoPlaylists
    }
}