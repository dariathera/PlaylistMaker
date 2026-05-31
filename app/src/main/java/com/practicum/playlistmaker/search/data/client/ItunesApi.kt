package com.practicum.playlistmaker.search.data.client

import com.practicum.playlistmaker.search.data.dto.ItunesApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApi {
    @GET("/search?entity=song")
    suspend fun search(
        @Query("term") text: String,
    ): ItunesApiResponse
}