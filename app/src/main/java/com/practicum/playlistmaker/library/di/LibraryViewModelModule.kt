package com.practicum.playlistmaker.library.di

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.practicum.playlistmaker.library.ui.viewmodel.FavoritesViewModel
import com.practicum.playlistmaker.library.ui.viewmodel.PlaylistContentViewModel
import com.practicum.playlistmaker.library.ui.viewmodel.PlaylistFormViewModel
import com.practicum.playlistmaker.library.ui.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val libraryViewModelModule = module {
    viewModel {
        FavoritesViewModel(get())
    }

    viewModel {
        PlaylistsViewModel(
            get(),
            get())
    }

    viewModel { (savedStateHandle: SavedStateHandle, playlistId: Long) ->
        PlaylistFormViewModel(
            createPlaylistUseCase = get(),
            context = get(),
            savedStateHandle = savedStateHandle,
            playlistId = playlistId,
            playlistInteractor = get(),
            privateStorageApi = get(),
            updatePlaylistUseCase = get()
        )
    }

    viewModel { (playlistId: Long, context: Context) ->
        PlaylistContentViewModel(
            playlistId,
            get(),
            get(),
            get(),
            get { parametersOf(context) }
        )
    }
}