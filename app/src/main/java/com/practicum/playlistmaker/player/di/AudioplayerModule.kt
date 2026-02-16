package com.practicum.playlistmaker.player.di

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.ui.timer.TimerManager
import org.koin.dsl.module
import android.os.Handler
import android.os.Looper
import com.practicum.playlistmaker.player.ui.timer.TimerManagerImpl

val audioplayerModule = module {

    single<Handler> {
        Handler(Looper.getMainLooper())
    }

    single<MediaPlayer> {
        MediaPlayer()
    }

    factory<TimerManager> {
        TimerManagerImpl(get(), get())
    }

}