package com.practicum.playlistmaker.player.di

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.ui.timer.TimerManager
import org.koin.dsl.module
import com.practicum.playlistmaker.player.ui.timer.TimerManagerImpl
import com.practicum.playlistmaker.player.ui.viewmodel.AudioplayerViewModel

val audioplayerModule = module {


    factory<MediaPlayer> {
        MediaPlayer()
    }

    factory<TimerManager> { (mediaPlayer: MediaPlayer) ->
        TimerManagerImpl(mediaPlayer)
    }

}