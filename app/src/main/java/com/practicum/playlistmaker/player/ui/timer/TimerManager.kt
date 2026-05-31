package com.practicum.playlistmaker.player.ui.timer

abstract class TimerManager() {
    abstract fun startTimer()
    abstract fun stopTimer()
    abstract fun clearTasks()
    abstract fun addListener(listner: TimeTextObserving)
}