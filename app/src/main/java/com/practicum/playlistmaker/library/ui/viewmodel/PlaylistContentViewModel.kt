package com.practicum.playlistmaker.library.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.domain.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.PrivateStorageApi
import com.practicum.playlistmaker.library.domain.entities.Playlist
import com.practicum.playlistmaker.library.ui.activity.PlaylistInfoState
import com.practicum.playlistmaker.root.ui.viewmodel.SharedViewModel
import com.practicum.playlistmaker.search.domain.entities.Track
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaylistContentViewModel(
    private val playlistId: Long,
    private val playlistInteractor: PlaylistInteractor,
    private val privateStorageApi: PrivateStorageApi,
    private val context: Context,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    companion object {
        private const val MILLIS_IN_MINUTE = 60000.0
    }

    private val forcedReturnLiveData = SingleLiveEvent<Unit>()
    fun observeForcedReturn(): LiveData<Unit> = forcedReturnLiveData

    private val playlistInfoLiveData = MutableLiveData<PlaylistInfoState>(PlaylistInfoState())
    fun observePlaylistInfo(): LiveData<PlaylistInfoState> = playlistInfoLiveData

    private val trackListLiveData = MutableLiveData<MutableList<Track>>(mutableListOf<Track>())
    fun observeTrackList(): LiveData<MutableList<Track>> = trackListLiveData


    init {
        updateState()
    }

    fun updateState() {
        viewModelScope.launch(Dispatchers.IO) {
            val playlist: Playlist? = playlistInteractor.getPlaylistById(playlistId)
            if (playlist == null) {
                Log.e(App.Companion.ERROR_LOG_TAG, "В конструктор PlaylistContentViewеModel передан id несуществующего плейлиста")
                forcedReturnLiveData.postValue(Unit)
            } else {
                val totalDurationInMillis = playlist.trackList.sumOf { it.trackTime }
                playlistInfoLiveData.postValue(PlaylistInfoState(
                    playlist.playlist.name,
                    playlist.playlist.description,
                    Math.round(totalDurationInMillis / MILLIS_IN_MINUTE).toInt(),
                    playlist.trackList.size,
                    privateStorageApi.getFileUri(playlist.playlist.coverFileName)
                ))
                trackListLiveData.postValue(playlist.trackList)
            }
        }
    }

    fun deleteTrackFromPlaylist(trackId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(NonCancellable) {
                playlistInteractor.deleteTrackFromPlaylist(
                    trackId=trackId,
                    playlistId=playlistId
                )
            }
            updateState()
        }
    }

    fun sharePlaylist(sharedViewModel: SharedViewModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val playlist: Playlist? = playlistInteractor.getPlaylistById(playlistId)
            if (playlist != null) {
                if (playlist.trackList.isEmpty()) {
                    sharedViewModel.setToastMessage(
                        context.getString(R.string.no_tracks_to_share)
                    )
                } else {
                    sharingInteractor.sharePlaylist(playlist)
                }
            }
            Log.e(App.Companion.ERROR_LOG_TAG, "В PlaylistContentViewModel передан недействительный playlistId")
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(NonCancellable) {
                playlistInteractor.deletePlaylistById(playlistId)
            }
        }
    }

}