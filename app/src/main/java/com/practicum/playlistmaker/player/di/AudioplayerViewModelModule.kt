package com.practicum.playlistmaker.player.di

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.ui.viewmodel.AudioplayerViewModel
import com.practicum.playlistmaker.search.domain.entities.Track
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val audioplayerViewModelModule = module {
    viewModel { (track: Track) ->

        val mediaPlayer : MediaPlayer = get()

        AudioplayerViewModel(
            track,
            get(),
            get(),
            get(),
            get()
        )
    }
}
