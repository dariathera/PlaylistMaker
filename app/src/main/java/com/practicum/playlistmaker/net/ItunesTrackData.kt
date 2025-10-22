package com.practicum.playlistmaker.net

data class ItunesTrackData(val trackName : String,
                           val artistName : String,
                           val trackTimeMillis : Int,
                           val artworkUrl100 : String,
                           val trackId : Int,
                           val country: String?,
                           val primaryGenreName: String?,
                           val collectionName: String?,
                           val releaseDate: String? // нужны первые 4 символа
)
