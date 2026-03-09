package com.practicum.playlistmaker.library.ui.activity

import com.practicum.playlistmaker.library.domain.Playlist

sealed interface  PlaylistsState {
    object NoPlaylists : PlaylistsState
    data class userPlaylists(val playlists: MutableList<Playlist>) : PlaylistsState
}