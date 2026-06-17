package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.db.data.AppDatabase
import com.practicum.playlistmaker.search.data.client.NetworkClient
import com.practicum.playlistmaker.search.data.dto.GetTracksRequest
import com.practicum.playlistmaker.search.data.dto.NetResponse
import com.practicum.playlistmaker.search.domain.entities.Track
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val appDatabase: AppDatabase
): TracksRepository {

    override fun getMusic(expression: String) : Flow<Resource<MutableList<Track>>> = flow {

        val response : NetResponse = networkClient.doRequest(GetTracksRequest(expression))

        when(response) {
            is NetResponse.BlankResponse ->
                emit(
                    Resource.Success<MutableList<Track>>(
                        mutableListOf<Track>()
                    )
                )
            is NetResponse.GoodNetResponse -> {
                val favoriteIdList = appDatabase.getTrackDao().getTrackIds()
                emit(
                    Resource.Success<MutableList<Track>>(
                        response.results.map { result ->
                            val track = Track(
                                trackName = result.trackName,
                                artistName = result.artistName,
                                trackTime = result.trackTimeMillis,
                                artworkUrl100 = result.artworkUrl100,
                                trackId = result.trackId,
                                album = result.collectionName,
                                year = result.releaseDate?.substring(0, 4)?.toInt(),
                                genre = result.primaryGenreName,
                                country = result.country,
                                previewUrl = result.previewUrl
                            )
                            track.isFavorite = track.trackId in favoriteIdList
                            track
                        }.toMutableList()
                    )
                )
            }
            is NetResponse.ServerError ->
                emit(
                    Resource.Error<MutableList<Track>>(
                        "Ошибка сервера, код ${response.resultCode}"
                    )
                )
            NetResponse.NetConnectionError ->
                emit(
                    Resource.Error<MutableList<Track>>(
                        "Нет интернет-соединения на устройстве"
                    )
                )
            NetResponse.UnknownError ->
                emit(
                    Resource.Error<MutableList<Track>>(
                        "Неизвестная ошибка на стороне клиента"
                    )
                )
        }
    }
}