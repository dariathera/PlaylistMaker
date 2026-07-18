package com.practicum.playlistmaker.library.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.PrivateStorageApi
import com.practicum.playlistmaker.library.domain.entities.PlaylistGeneralInformation
import com.practicum.playlistmaker.library.ui.activity.PlaylistsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val privateStorageApi: PrivateStorageApi
) : ViewModel() {
    private val stateLiveData = MutableLiveData<PlaylistsState>(
        PlaylistsState.NoPlaylists
    )
    fun observeState(): LiveData<PlaylistsState> = stateLiveData

    init {
        updateState()
    }

    fun updateState() {
        viewModelScope.launch(Dispatchers.IO) {
            val playlists : List<PlaylistGeneralInformation> = playlistInteractor.getAllPlaylistsGeneralInfo()
            if (playlists.size > 0) {
                for (playlist in playlists) {
                    playlist.uri = privateStorageApi.getFileUri(playlist.coverFileName)
                }
                stateLiveData.postValue(PlaylistsState.UserPlaylists(playlists))
            } else {
                stateLiveData.postValue(PlaylistsState.NoPlaylists)
            }
        }
    }
}