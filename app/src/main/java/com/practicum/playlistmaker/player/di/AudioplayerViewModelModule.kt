package com.practicum.playlistmaker.player.di

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.ui.viewmodel.AudioplayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val audioplayerViewModelModule = module {
    viewModel { (trackUrl: String?) ->

        val mediaPlayer : MediaPlayer = get()

        AudioplayerViewModel(
            trackUrl,
            mediaPlayer,
            get {parametersOf(mediaPlayer)}
        )
    }
}
