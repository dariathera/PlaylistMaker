package com.practicum.playlistmaker.player.di

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.ui.viewmodel.AudioplayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import com.practicum.playlistmaker.search.domain.entities.Track

val audioplayerViewModelModule = module {
    viewModel { (track: Track) ->

        val mediaPlayer : MediaPlayer = get()

        AudioplayerViewModel(
            track,
            mediaPlayer,
            get {parametersOf(mediaPlayer)},
            get()
        )
    }
}
