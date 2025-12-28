package com.practicum.playlistmaker.domain.repository

interface SettingsRepository {
    fun switchColorTheme(darkThemeEnabled: Boolean)
    fun getColorTheme() : Boolean
}
