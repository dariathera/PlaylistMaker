package com.practicum.playlistmaker.search_history.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.search_history.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search_history.data.StringSearchHistorySaverClient
import com.practicum.playlistmaker.search_history.domain.GetHistoryInteractor
import com.practicum.playlistmaker.search_history.domain.GetHistoryInteractorImpl
import com.practicum.playlistmaker.search_history.domain.SearchHistoryRepository
import com.practicum.playlistmaker.util.Saver
import org.koin.core.qualifier.named
import org.koin.dsl.module

val searchHistoryModule = module {

    single<SharedPreferences?> {
        App.Companion.getInstance()?.getSharedPreferences(
            App.Companion.getInstance()?.USER_SETTINGS_PREFERENCES,
            Context.MODE_PRIVATE
        )
    }

    single<Saver<String>>(named("historySaver")) {
        StringSearchHistorySaverClient(
            get(),
            App.Companion.getInstance()?.SEARCH_HISTORY_KEY
        )
    }

    single<Gson> { Gson() }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(
            get(named("historySaver")),
            get())
    }

    single<GetHistoryInteractor> {
        GetHistoryInteractorImpl(get())
    }

}