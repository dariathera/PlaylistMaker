package com.practicum.playlistmaker.settings.domain

interface SettingsRepository {
    fun switchColorTheme(darkThemeEnabled: Boolean)
    fun getColorTheme() : Boolean
}