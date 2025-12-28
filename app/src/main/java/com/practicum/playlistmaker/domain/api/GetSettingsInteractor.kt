package com.practicum.playlistmaker.domain.api

interface GetSettingsInteractor {
    fun switchColorTheme(darkThemeEnabled: Boolean)
    fun getColorTheme() : Boolean
}