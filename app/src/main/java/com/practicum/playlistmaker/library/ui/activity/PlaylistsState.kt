package com.practicum.playlistmaker.library.ui.activity

import com.practicum.playlistmaker.library.domain.entities.PlaylistGeneralInformation

sealed interface  PlaylistsState {
    object NoPlaylists : PlaylistsState
    data class UserPlaylists(val playlists: List<PlaylistGeneralInformation>) : PlaylistsState
}