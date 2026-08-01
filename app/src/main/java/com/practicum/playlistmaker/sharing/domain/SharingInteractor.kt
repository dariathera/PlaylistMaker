package com.practicum.playlistmaker.sharing.domain

import com.practicum.playlistmaker.library.domain.entities.Playlist

interface SharingInteractor {
    fun shareApp()
    fun openTerms()
    fun openSupport()
    fun sharePlaylist(playlist: Playlist)
}