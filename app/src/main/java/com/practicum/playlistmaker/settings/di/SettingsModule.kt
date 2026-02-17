package com.practicum.playlistmaker.settings.di

import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.data.SettingsSaverClient
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.SettingsInteractorImpl
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.util.Saver
import org.koin.core.qualifier.named
import org.koin.dsl.module

val settingsModule = module {
    single<Saver<Boolean>>(named("settingsSaver")) {
        SettingsSaverClient(
            get(),
            App.Companion.getInstance()?.DARK_THEME_KEY,
            App.Companion.getInstance()
        )
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(
            get(named("settingsSaver")),
            App.Companion.getInstance()
        )
    }

    factory<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }
}

