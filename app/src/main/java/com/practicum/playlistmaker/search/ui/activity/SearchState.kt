package com.practicum.playlistmaker.search.ui.activity

import com.practicum.playlistmaker.search.domain.entities.Track

sealed interface SearchState {
    object Loading : SearchState
    object Blank : SearchState
    data class History(val tracks: MutableList<Track>) : SearchState
    data class FoundTracks(val tracks: MutableList<Track>) : SearchState
    object NothingFound : SearchState
    object NoInternet : SearchState
}