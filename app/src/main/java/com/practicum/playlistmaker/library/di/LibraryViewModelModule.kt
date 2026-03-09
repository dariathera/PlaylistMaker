package com.practicum.playlistmaker.library.di

import com.practicum.playlistmaker.library.ui.viewmodel.FavoritesViewModel
import com.practicum.playlistmaker.library.ui.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val libraryViewModelModule = module {
    viewModel {
        FavoritesViewModel()
    }

    viewModel {
        PlaylistsViewModel()
    }
}