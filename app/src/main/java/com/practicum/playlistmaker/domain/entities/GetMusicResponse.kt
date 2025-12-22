package com.practicum.playlistmaker.domain.entities

sealed interface GetMusicResponse

data class GoodResponse(val tracks: MutableList<Track>) : GetMusicResponse
object BadResponse : GetMusicResponse
object EmptyResponse : GetMusicResponse