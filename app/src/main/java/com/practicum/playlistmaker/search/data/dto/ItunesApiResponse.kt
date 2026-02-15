package com.practicum.playlistmaker.search.data.dto

import com.practicum.playlistmaker.search.data.dto.ItunesTrackData

data class ItunesApiResponse(
    val resultCount: Int,
    val results: List<ItunesTrackData>
)