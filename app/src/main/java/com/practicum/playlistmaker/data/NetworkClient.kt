package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.dto.NetResponse

interface NetworkClient {
    fun doRequest(dto: Any): NetResponse
}