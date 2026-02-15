package com.practicum.playlistmaker.player.ui.timer

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import java.util.Locale

class TimerManagerImpl(private val mediaPlayer : MediaPlayer, private val listener : TimeTextObserving
) : TimerManager() {
    private val TIMER_DELAY = 1000L
    private val MAX_TRACK_TIME = 30000L
    override val START_TIME_TEXT = "00:00"
    private var handler : Handler = Handler(Looper.getMainLooper())

    override val timerRunnable = object : Runnable {
        override fun run() {
            val currentTime = updateTimerText()
            if (currentTime < MAX_TRACK_TIME) {
                handler.postDelayed(this, TIMER_DELAY)
                listener.setNewTimeText(SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(currentTime))
            }
        }
    }

    override fun startTimer() {
        handler.postDelayed(timerRunnable, TIMER_DELAY)
    }

    override fun stopTimer() {
        handler.removeCallbacks(timerRunnable)
        updateTimerText()
    }

    // Перед вызовом нужно убедиться, что трек, для которого вызывается метод, существует
    fun updateTimerText() : Int {
        val currentTime : Int = mediaPlayer.currentPosition
        return currentTime
    }

    override fun clearHandler() {
        handler.removeCallbacks(timerRunnable)
    }



}