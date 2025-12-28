package com.practicum.playlistmaker.data.network

import android.util.Log
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.GetTracksRequest
import com.practicum.playlistmaker.data.dto.NetResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ItunesNetworkClient : NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val itunesService = retrofit.create(ItunesApi::class.java)

    override fun doRequest(dto: Any): NetResponse {
        if (dto is GetTracksRequest) {
            return try {
                val resp = itunesService.search(dto.expression).execute()
                if (resp.isSuccessful) {
                    resp.body() ?: NetResponse()
                } else {
                    Log.e(App.ERROR_LOG_TAG, "HTTP ошибка: ${resp.code()} - ${resp.message()}")
                    NetResponse()
                }
            } catch (e: Exception) {
                Log.e(App.ERROR_LOG_TAG, "Неизвестная ошибка интернет-соединения: ${e.localizedMessage}")
                NetResponse()
            }
        } else {
            Log.e(App.ERROR_LOG_TAG, "В itunesNetworkClient.doRequest передан некорректный тип данных.")
            return NetResponse()
        }
    }

    companion object {
        const val ITUNES_BASE_URL = "https://itunes.apple.com"
    }
}