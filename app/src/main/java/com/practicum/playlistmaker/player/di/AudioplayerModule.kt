package com.practicum.playlistmaker.player.di

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.ui.timer.TimerManager
import com.practicum.playlistmaker.player.ui.timer.TimerManagerImpl
import org.koin.dsl.module

val audioplayerModule = module {

    factory<MediaPlayer> {
        MediaPlayer()
    }

    factory<TimerManager> { (mediaPlayer: MediaPlayer) ->
        TimerManagerImpl(mediaPlayer)
    }

}