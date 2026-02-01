package com.practicum.playlistmaker.settings.domain

class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {
    override fun getColorTheme() : Boolean {
        return repository.getColorTheme()
    }

    override fun switchColorTheme(darkThemeEnabled: Boolean) {
        repository.switchColorTheme(darkThemeEnabled)
    }
}