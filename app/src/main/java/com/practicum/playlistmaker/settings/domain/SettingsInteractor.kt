package com.practicum.playlistmaker.settings.domain

interface SettingsInteractor {
    fun switchColorTheme(darkThemeEnabled: Boolean)
    fun getColorTheme() : Boolean
}