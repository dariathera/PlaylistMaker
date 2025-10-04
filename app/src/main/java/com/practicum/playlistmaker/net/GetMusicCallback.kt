package com.practicum.playlistmaker.net

import com.practicum.playlistmaker.data.objects.Track

sealed interface GetMusicResponse

data class GoodResponse(val tracks: MutableList<Track>) : GetMusicResponse
object BadResponse : GetMusicResponse
object EmptyResponse : GetMusicResponse