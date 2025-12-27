package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.GetSettingsInteractor
import com.practicum.playlistmaker.domain.repository.SettingsSaverRepository

class GetSettingsInteractorImpl(private val repository: SettingsSaverRepository) : GetSettingsInteractor {
    override fun getColorTheme() : Boolean {
        return repository.getColorTheme()
    }

    override fun switchColorTheme(darkThemeEnabled: Boolean) {
        repository.switchColorTheme(darkThemeEnabled)
    }
}