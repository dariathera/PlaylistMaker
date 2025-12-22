package com.practicum.playlistmaker.domain.repository

interface SettingsSaverRepository {
    fun switchColorTheme(darkThemeEnabled: Boolean)
    fun getColorTheme() : Boolean
}
