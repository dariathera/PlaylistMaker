package com.practicum.playlistmaker.player.ui.viewmodel

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
import com.practicum.playlistmaker.search.domain.entities.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AudioplayerViewModel(
    private val track: Track,
    private val mediaPlayer : MediaPlayer,
    private val timerManager : TimerManager,
    private val favoritesInteractor : FavoritesInteractor
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

    private val isFavoriteLiveData = MutableLiveData<Boolean>(track.isFavorite)
    fun observeIsFavorite(): LiveData<Boolean> = isFavoriteLiveData

    init {
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
            if (track.isFavorite) {
                favoritesInteractor.deleteFavorite(track)
            } else {
                favoritesInteractor.addNewFavorite(track)
            }
            isFavoriteLiveData.postValue(track.isFavorite)
        }
    }

    companion object {
        private const val START_TIME_TEXT = "00:00"
    }

}
