package com.practicum.playlistmaker.library.di

import com.practicum.playlistmaker.library.data.FavoritesApiImpl
import com.practicum.playlistmaker.library.data.PlaylistApiImpl
import com.practicum.playlistmaker.library.data.PrivateStorageApiImpl
import com.practicum.playlistmaker.library.domain.CreatePlaylistUseCase
import com.practicum.playlistmaker.library.domain.CreatePlaylistUseCaseImpl
import com.practicum.playlistmaker.library.domain.FavoritesApi
import com.practicum.playlistmaker.library.domain.FavoritesInteractor
import com.practicum.playlistmaker.library.domain.FavoritesInteractorImpl
import com.practicum.playlistmaker.library.domain.PlaylistApi
import com.practicum.playlistmaker.library.domain.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.PlaylistInteractorImpl
import com.practicum.playlistmaker.library.domain.PrivateStorageApi
import com.practicum.playlistmaker.library.domain.UpdatePlaylistUseCase
import com.practicum.playlistmaker.library.domain.UpdatePlaylistUseCaseImpl
import org.koin.dsl.module

val libraryModule = module {
    single<FavoritesApi> {
        FavoritesApiImpl(get())
    }

    single<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }

    single<PlaylistApi> {
        PlaylistApiImpl(get())
    }

    single<PrivateStorageApi> {
        PrivateStorageApiImpl(get())
    }

    single<PlaylistInteractor> {
        PlaylistInteractorImpl(
            get(),
            get()
        )
    }

    single<CreatePlaylistUseCase> {
        CreatePlaylistUseCaseImpl(
            get(),
            get()
        )
    }

    single<UpdatePlaylistUseCase> {
        UpdatePlaylistUseCaseImpl(
            get(),
            get()
        )
    }
}