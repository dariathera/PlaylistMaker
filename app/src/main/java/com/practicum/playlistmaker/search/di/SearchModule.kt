package com.practicum.playlistmaker.search.di

import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.search.data.TracksRepositoryImpl
import com.practicum.playlistmaker.search.data.client.ItunesApi
import com.practicum.playlistmaker.search.data.client.ItunesNetworkClient
import com.practicum.playlistmaker.search.data.client.NetworkClient
import com.practicum.playlistmaker.search.domain.GetTracksInteractor
import com.practicum.playlistmaker.search.domain.GetTracksInteractorImpl
import com.practicum.playlistmaker.search.domain.TracksRepository
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val searchModule = module {

    single<ItunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApi::class.java)
    }

    single<NetworkClient> {
        ItunesNetworkClient(
            App.getContext(),
            get())
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    factory<GetTracksInteractor> {
        GetTracksInteractorImpl(get())
    }
}