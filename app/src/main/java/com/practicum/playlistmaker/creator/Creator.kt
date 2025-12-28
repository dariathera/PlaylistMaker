package com.practicum.playlistmaker.creator

import android.media.MediaPlayer
import android.widget.TextView
import com.practicum.playlistmaker.data.network.ItunesNetworkClient
import com.practicum.playlistmaker.data.preferences.SettingsSaverClient
import com.practicum.playlistmaker.data.preferences.StringSearchHistorySaverClient
import com.practicum.playlistmaker.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.data.repository.SettingsRepositoryImpl
import com.practicum.playlistmaker.domain.api.GetHistoryInteractor
import com.practicum.playlistmaker.domain.api.GetTracksInteractor
import com.practicum.playlistmaker.domain.api.GetSettingsInteractor
import com.practicum.playlistmaker.domain.api.UserMakesTracksRequestUseCase
import com.practicum.playlistmaker.domain.interactors.GetHistoryInteractorImpl
import com.practicum.playlistmaker.domain.interactors.GetTracksInteractorImpl
import com.practicum.playlistmaker.domain.interactors.GetSettingsInteractorImpl
import com.practicum.playlistmaker.domain.repository.TracksRepository
import com.practicum.playlistmaker.domain.repository.SearchHistoryRepository
import com.practicum.playlistmaker.domain.repository.SettingsRepository
import com.practicum.playlistmaker.domain.use_cases.UserMakesTracksRequestUseCaseImpl
import com.practicum.playlistmaker.presentation.TimerManager
import com.practicum.playlistmaker.presentation.TimerManagerImpl

object Creator {
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(ItunesNetworkClient())
    }

    fun provideGetTracksInteractor(): GetTracksInteractor {
        return GetTracksInteractorImpl(getTracksRepository())
    }

    fun provideUserMakesTracksRequestUseCase() : UserMakesTracksRequestUseCase {
        return UserMakesTracksRequestUseCaseImpl()
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

    fun provideGetSettingsInteractor(): GetSettingsInteractor {
        return GetSettingsInteractorImpl(getSettingsSaverRepository())
    }

    //--------------------
    fun provideTimerManager(txtCurrentTime : TextView, mediaPlayer : MediaPlayer): TimerManager {
        return TimerManagerImpl(txtCurrentTime, mediaPlayer)
    }
}