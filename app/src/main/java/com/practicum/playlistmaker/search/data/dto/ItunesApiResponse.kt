package com.practicum.playlistmaker.search.data.dto

data class ItunesApiResponse(
    val resultCount: Int,
    val results: List<ItunesTrackData>
)