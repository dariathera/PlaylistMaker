package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.data.dto.GetTracksRequest
import com.practicum.playlistmaker.data.dto.ItunesResponse
import com.practicum.playlistmaker.data.dto.NetResponse
import com.practicum.playlistmaker.data.network.ItunesNetworkClient
import com.practicum.playlistmaker.domain.entities.BadResponse
import com.practicum.playlistmaker.domain.entities.EmptyResponse
import com.practicum.playlistmaker.domain.entities.GetMusicResponse
import com.practicum.playlistmaker.domain.entities.GoodResponse
import com.practicum.playlistmaker.domain.entities.Track
import com.practicum.playlistmaker.domain.repository.TracksRepository
import java.text.SimpleDateFormat
import java.util.Locale

class TracksRepositoryImpl(private val itunesNetworkClient: ItunesNetworkClient):
    TracksRepository {

    override fun getMusic(expression: String) : GetMusicResponse{
        val response : NetResponse = itunesNetworkClient.doRequest(GetTracksRequest(expression))
        val itunesResponse = response as? ItunesResponse

        if (itunesResponse == null) {
            return BadResponse
        } else {
            if (itunesResponse.resultCount == 0) {
                return EmptyResponse
            } else {
                return GoodResponse(
                    itunesResponse.results.map { result ->
                        Track(
                            trackName = result.trackName,
                            artistName = result.artistName,
                            trackTime = SimpleDateFormat(
                                "mm:ss",
                                Locale.getDefault()
                            ).format(result.trackTimeMillis),
                            artworkUrl100 = result.artworkUrl100,
                            trackId = result.trackId,
                            album = result.collectionName,
                            year = result.releaseDate?.substring(0, 4)?.toInt(),
                            genre = result.primaryGenreName,
                            country = result.country,
                            previewUrl = result.previewUrl
                        )
                    }.toMutableList()
                )
            }
        }
    }
}

