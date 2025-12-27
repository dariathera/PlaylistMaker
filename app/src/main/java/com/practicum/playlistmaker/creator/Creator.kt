package com.practicum.playlistmaker.creator

import com.practicum.playlistmaker.data.network.ItunesNetworkClient
import com.practicum.playlistmaker.data.preferences.SettingsSaverClient
import com.practicum.playlistmaker.data.preferences.StringSearchHistorySaverClient
import com.practicum.playlistmaker.data.repository.GetMusicRepositoryImpl
import com.practicum.playlistmaker.data.repository.SearchHistorySaverRepositoryImpl
import com.practicum.playlistmaker.data.repository.SettingsSaverRepositoryImpl
import com.practicum.playlistmaker.domain.api.GetHistoryInteractor
import com.practicum.playlistmaker.domain.api.GetMusicInteractor
import com.practicum.playlistmaker.domain.api.GetSettingsInteractor
import com.practicum.playlistmaker.domain.impl.GetHistoryInteractorImpl
import com.practicum.playlistmaker.domain.impl.GetMusicInteractorImpl
import com.practicum.playlistmaker.domain.impl.GetSettingsInteractorImpl
import com.practicum.playlistmaker.domain.repository.GetMusicRepository
import com.practicum.playlistmaker.domain.repository.SearchHistorySaverRepository
import com.practicum.playlistmaker.domain.repository.SettingsSaverRepository

object Creator {
    private fun getMusicRepository(): GetMusicRepository {
        return GetMusicRepositoryImpl(ItunesNetworkClient())
    }

    fun provideGetMusicInteractor(): GetMusicInteractor {
        return GetMusicInteractorImpl(getMusicRepository())
    }

    //--------------------
    private fun getSearchHistorySaverRepository(): SearchHistorySaverRepository {
        return SearchHistorySaverRepositoryImpl(StringSearchHistorySaverClient())
    }

    fun provideGetHistoryInteractor(): GetHistoryInteractor {
        return GetHistoryInteractorImpl(getSearchHistorySaverRepository())
    }


    //--------------------
    private fun getSettingsSaverRepository(): SettingsSaverRepository {
        return SettingsSaverRepositoryImpl(SettingsSaverClient())
    }

    fun provideGetSettingsInteractor(): GetSettingsInteractor {
        return GetSettingsInteractorImpl(getSettingsSaverRepository())
    }
}