package com.practicum.playlistmaker.library.di

import com.practicum.playlistmaker.library.ui.viewmodel.FavoritesViewModel
import com.practicum.playlistmaker.library.ui.viewmodel.PlaylistCreatorViewModel
import com.practicum.playlistmaker.library.ui.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

import androidx.lifecycle.SavedStateHandle

val libraryViewModelModule = module {
    viewModel {
        FavoritesViewModel(get())
    }

    viewModel {
        PlaylistsViewModel(
            get(),
            get())
    }

    viewModel { (savedStateHandle: SavedStateHandle) ->
        PlaylistCreatorViewModel(
            createPlaylistUseCase = get(),
            context = get(),
            savedStateHandle = savedStateHandle
        )
    }
}