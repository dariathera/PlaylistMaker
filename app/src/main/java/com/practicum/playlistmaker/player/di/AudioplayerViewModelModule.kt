package com.practicum.playlistmaker.player.di

import com.practicum.playlistmaker.player.ui.viewmodel.AudioplayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val audioplayerViewModelModule = module {
    viewModel { (trackUrl: String?) ->
        AudioplayerViewModel(trackUrl, get(), get(), get())
    }
}
