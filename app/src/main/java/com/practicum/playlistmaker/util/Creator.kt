package com.practicum.playlistmaker.util

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.widget.TextView
import com.practicum.playlistmaker.player.ui.timer.TimeTextObserving
import com.practicum.playlistmaker.search.data.client.ItunesNetworkClient
import com.practicum.playlistmaker.settings.data.SettingsSaverClient
import com.practicum.playlistmaker.search_history.data.StringSearchHistorySaverClient
import com.practicum.playlistmaker.search.data.TracksRepositoryImpl
import com.practicum.playlistmaker.search_history.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.search_history.domain.GetHistoryInteractor
import com.practicum.playlistmaker.search.domain.GetTracksInteractor
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.search.domain.UserMakesTracksRequestUseCase
import com.practicum.playlistmaker.search_history.domain.GetHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.GetTracksInteractorImpl
import com.practicum.playlistmaker.settings.domain.SettingsInteractorImpl
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.search_history.domain.SearchHistoryRepository
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.search.domain.UserMakesTracksRequestUseCaseImpl
import com.practicum.playlistmaker.player.ui.timer.TimerManager
import com.practicum.playlistmaker.player.ui.timer.TimerManagerImpl
import com.practicum.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.SharingInteractorImpl

object Creator {
    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(ItunesNetworkClient(context))
    }

    fun provideGetTracksInteractor(context: Context): GetTracksInteractor {
        return GetTracksInteractorImpl(getTracksRepository(context))
    }

    fun provideUserMakesTracksRequestUseCase(context: Context) : UserMakesTracksRequestUseCase {
        return UserMakesTracksRequestUseCaseImpl(context)
    }

    //--------------------
    private fun getSearchHistorySaverRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(StringSearchHistorySaverClient())
    }

    fun provideGetHistoryInteractor(): GetHistoryInteractor {
        return GetHistoryInteractorImpl(getSearchHistorySaverRepository())
    }


    //--------------------
    private fun getSettingsSaverRepository(): SettingsRepository {
        return SettingsRepositoryImpl(SettingsSaverClient())
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsSaverRepository())
    }

    //--------------------
    fun provideTimerManager(mediaPlayer : MediaPlayer, listener : TimeTextObserving): TimerManager {
        return TimerManagerImpl(mediaPlayer, listener)
    }

    //--------------------
    private fun getExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }
    fun provideSharingInteractor(context: Context) : SharingInteractor {
        return SharingInteractorImpl(
            context,
            getExternalNavigator(context))
    }
}