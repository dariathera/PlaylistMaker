package com.practicum.playlistmaker.player.ui.viewmodel

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.player.ui.mediaplayer.MediaplayerState
import com.practicum.playlistmaker.player.ui.timer.TimeTextObserving
import com.practicum.playlistmaker.player.ui.timer.TimerManager
import com.practicum.playlistmaker.util.SingleLiveEvent
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.domain.FavoritesInteractor
import com.practicum.playlistmaker.library.domain.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.PrivateStorageApi
import com.practicum.playlistmaker.library.domain.entities.Playlist
import com.practicum.playlistmaker.library.domain.entities.PlaylistGeneralInformation
import com.practicum.playlistmaker.root.ui.viewmodel.SharedViewModel
import com.practicum.playlistmaker.search.domain.entities.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AudioplayerViewModel(
    private val track: Track,
    private val mediaPlayer : MediaPlayer,
    private val timerManager : TimerManager,
    private val favoritesInteractor : FavoritesInteractor,
    private val playlistInteractor: PlaylistInteractor,
    private val privateStorageApi: PrivateStorageApi,
    private val context: Context
) : ViewModel(), TimeTextObserving {

    // Для возобновления воспроизведения после поворота
    private var savedPlayerPosition: Int = 0
    private var savedIsPlaying: Boolean = false
    private var playerState : MediaplayerState = MediaplayerState.DEFAULT

    private val isPlayingLiveData = MutableLiveData<Boolean>(false)
    fun observeIsPlaying(): LiveData<Boolean> = isPlayingLiveData

    private val timeTextLiveData = MutableLiveData<String>(START_TIME_TEXT)
    fun observeTimeText(): LiveData<String> = timeTextLiveData

    private val showMessageLiveData = SingleLiveEvent<String>()
    fun observeShowMessage(): LiveData<String> = showMessageLiveData

    private val isFavoriteLiveData = MutableLiveData<Boolean>(false)
    fun observeIsFavorite(): LiveData<Boolean> = isFavoriteLiveData

    private val playlistsLiveData = MutableLiveData<List<PlaylistGeneralInformation>>(
        listOf<PlaylistGeneralInformation>()
    )
    fun observePlaylists(): LiveData<List<PlaylistGeneralInformation>> = playlistsLiveData

    private val hideBottomSheetLiveData = SingleLiveEvent<Unit>()
    fun observeHideBottomSheet(): LiveData<Unit> = hideBottomSheetLiveData

    init {
        postValueisFavoriteLiveData()
        preparePlayer()
        timerManager.addListener(this)
    }

    override fun onCleared() {
        // Сохраняем текущую позицию перед освобождением
        if (playerState == MediaplayerState.PLAYING || playerState == MediaplayerState.PAUSED) {
            savedPlayerPosition = mediaPlayer.currentPosition
            savedIsPlaying = playerState == MediaplayerState.PLAYING
        }

        mediaPlayer.release()
        timerManager.clearTasks()
        super.onCleared()
    }

    fun stopTimer() {
        timerManager.clearTasks()
    }

    // Управление воспроизведением
    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            if (savedPlayerPosition > 0) {
                mediaPlayer.seekTo(savedPlayerPosition)
            } else {
                timeTextLiveData.postValue(START_TIME_TEXT)
            }
            playerState = if (savedIsPlaying) {
                mediaPlayer.start()
                timerManager.startTimer()
                MediaplayerState.PLAYING
            } else {
                MediaplayerState.PREPARED
            }
            isPlayingLiveData.postValue(savedIsPlaying)
        }
        mediaPlayer.setOnCompletionListener {
            playerState = MediaplayerState.PREPARED
            isPlayingLiveData.postValue(false)
            timeTextLiveData.postValue(START_TIME_TEXT)
            timerManager.clearTasks()
            // Сбрасываем сохраненное состояние при завершении трека
            savedPlayerPosition = 0
            savedIsPlaying = false
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = MediaplayerState.PLAYING
        isPlayingLiveData.postValue(true)
    }

    fun pausePlayer() {
        mediaPlayer.pause()
        playerState = MediaplayerState.PAUSED
        isPlayingLiveData.postValue(false)
        // Сохраняем позицию при паузе
        savedPlayerPosition = mediaPlayer.currentPosition
    }

    fun playbackControl() {
        when(playerState) {
            MediaplayerState.PLAYING -> {
                pausePlayer()
                timerManager.stopTimer()
            }
            MediaplayerState.PREPARED, MediaplayerState.PAUSED -> {
                if (track.previewUrl != null) {
                    startPlayer()
                    timerManager.startTimer()
                } else {
                    showMessageLiveData.postValue(
                        App.getContext().getString(
                            R.string.no_preview_link))
                }

            }
            MediaplayerState.DEFAULT -> {
                Log.e(
                    App.Companion.ERROR_LOG_TAG, "Недопустимая ситуация: реализуется " +
                            "ветка DEFAULT в функции playbackControl(). Это значит, что ранее по " +
                            "какой-то причине функция preparePlayer() не была вызвана.")
            }
        }
    }

    override fun setNewTimeText(timeText: String) {
        timeTextLiveData.postValue(timeText)
    }

    fun onFavoriteClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            val trackIsFavorite = isFavoriteLiveData.value ?: false
            if (trackIsFavorite) {
                favoritesInteractor.deleteFavorite(track)
            } else {
                favoritesInteractor.addNewFavorite(track)
            }
            postValueisFavoriteLiveData()
        }
    }

    private fun postValueisFavoriteLiveData() {
        viewModelScope.launch(Dispatchers.IO) {
            val favoriteTracks: MutableList<Track> = mutableListOf<Track>()
            favoriteTracks.addAll(favoritesInteractor.getAllFavorites().firstOrNull() ?: emptyList())
            isFavoriteLiveData.postValue(track in favoriteTracks)
        }
    }

    fun requestPlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            val playlists : List<PlaylistGeneralInformation> = playlistInteractor.getAllPlaylistsGeneralInfo()
            for (playlist in playlists) {
                playlist.uri = privateStorageApi.getFileUri(playlist.coverFileName)
            }
            playlistsLiveData.postValue(playlists)
        }
    }

    fun handleClickOnPlaylist(id: Long, sharedViewModel: SharedViewModel){
        viewModelScope.launch(Dispatchers.IO) {
            val trackIsAlreadyIncluded = async { isTrackincludedById(id) }
            if (trackIsAlreadyIncluded.await()) {
                notifyTrackAlreadyAdded(id, sharedViewModel)
                } else {
                addTrackToPlaylist(id, sharedViewModel)
                hideBottomSheetLiveData.postValue(Unit)
            }
        }
    }

    private suspend fun notifyTrackAlreadyAdded(id: Long, sharedViewModel: SharedViewModel) {
        val playlistName : String? = playlistInteractor.getPlaylistNameByPlaylistId(id)
        if (!playlistName.isNullOrEmpty()) {
            sharedViewModel.setToastMessage(
                context.getString(R.string.track_already_added, playlistName)
            )
        }
    }

    private suspend fun isTrackincludedById(id: Long) : Boolean {
        val trackIdList: List<Long> = playlistInteractor.getTracksIdListByPlaylistId(id)
        var trackIsAlreadyIncluded = false
        for (element in trackIdList) {
            if (element == track.trackId) {
                trackIsAlreadyIncluded = true
                break
            }
        }
        return trackIsAlreadyIncluded
    }

    // Есть риск добавить трек повторно.
    // Я не проверяю, включён ли трек в плейлист -
    // это должно было быть на предыдущем этапе.
    private suspend fun addTrackToPlaylist(id: Long, sharedViewModel: SharedViewModel){
        val playlist: Playlist? = playlistInteractor.getPlaylistById(id)
        if (playlist != null) {
            playlist.trackList.add(track)
            playlistInteractor.updatePlaylist(playlist)
            sharedViewModel.setToastMessage(
                context.getString(R.string.added_to_playlist, playlist.playlist.name)
            )
            val playlistUpdated =  playlistInteractor.getPlaylistById(id)
        }
    }

    companion object {
        private const val START_TIME_TEXT = "00:00"
    }

}
