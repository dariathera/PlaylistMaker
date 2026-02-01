package com.practicum.playlistmaker.player.ui.timer

import androidx.lifecycle.LiveData

abstract class TimerManager() {

    abstract val START_TIME_TEXT : String
    abstract val timerRunnable : Runnable

    abstract fun startTimer()
    abstract fun stopTimer()
    abstract fun clearHandler()
}