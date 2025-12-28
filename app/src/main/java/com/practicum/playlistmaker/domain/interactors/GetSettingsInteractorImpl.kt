package com.practicum.playlistmaker.domain.interactors

import com.practicum.playlistmaker.domain.api.GetSettingsInteractor
import com.practicum.playlistmaker.domain.repository.SettingsRepository

class GetSettingsInteractorImpl(private val repository: SettingsRepository) : GetSettingsInteractor {
    override fun getColorTheme() : Boolean {
        return repository.getColorTheme()
    }

    override fun switchColorTheme(darkThemeEnabled: Boolean) {
        repository.switchColorTheme(darkThemeEnabled)
    }
}