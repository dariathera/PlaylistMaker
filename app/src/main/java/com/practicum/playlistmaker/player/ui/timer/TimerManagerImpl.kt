package com.practicum.playlistmaker.player.ui.timer

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class TimerManagerImpl(
    private val mediaPlayer : MediaPlayer
) : TimerManager() {
    private val listeners = mutableListOf<TimeTextObserving>()
    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var timerJob: Job? = null

    companion object {
        const val TIMER_DELAY = 300L
    }

    override fun startTimer() {
        timerJob?.cancel()
        timerJob = managerScope.launch {
            while (mediaPlayer.isPlaying) {
                val currentTime = updateTimerText()
                Log.d("TimerManager", "currentTime: ${currentTime}")

                for (listener in listeners) {
                    listener.setNewTimeText(SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(currentTime))
                }
                delay(TIMER_DELAY)
            }
        }
    }

    override fun stopTimer() {
        timerJob?.cancel()
    }

    // Перед вызовом нужно убедиться, что трек, для которого вызывается метод, существует
    fun updateTimerText() : Int {
        val currentTime : Int = mediaPlayer.currentPosition
        return currentTime
    }

    override fun clearTasks() {
        managerScope.coroutineContext.cancelChildren()
    }

    override fun addListener(listner: TimeTextObserving) {
        listeners.add(listner)
    }

}