package com.practicum.playlistmaker.search.data.client

import com.practicum.playlistmaker.search.data.dto.NetResponse

interface NetworkClient {
    suspend fun doRequest(dto: Any): NetResponse
}