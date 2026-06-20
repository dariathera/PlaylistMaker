package com.practicum.playlistmaker.library.di

import com.practicum.playlistmaker.library.data.FavoritesApiImpl
import com.practicum.playlistmaker.library.domain.FavoritesApi
import com.practicum.playlistmaker.library.domain.FavoritesInteractor
import com.practicum.playlistmaker.library.domain.FavoritesInteractorImpl
import org.koin.dsl.module

val libraryModule = module {
    single<FavoritesApi> {
        FavoritesApiImpl(get())
    }

    single<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }
}