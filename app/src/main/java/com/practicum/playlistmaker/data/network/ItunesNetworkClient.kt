package com.practicum.playlistmaker.data.network

import android.util.Log
import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.GetMusicRequest
import com.practicum.playlistmaker.data.dto.ItunesResponse
import com.practicum.playlistmaker.data.dto.NetResponse
import com.practicum.playlistmaker.domain.entities.Track
import com.practicum.playlistmaker.domain.entities.BadResponse
import com.practicum.playlistmaker.domain.entities.EmptyResponse
import com.practicum.playlistmaker.domain.entities.GetMusicResponse
import com.practicum.playlistmaker.domain.entities.GoodResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale

class ItunesNetworkClient : NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val itunesService = retrofit.create(ItunesApi::class.java)

    override fun doRequest(dto: Any): NetResponse {
        if (dto is GetMusicRequest) {
            return try {
                val resp = itunesService.search(dto.expression).execute()
                if (resp.isSuccessful) {
                    resp.body() ?: NetResponse()
                } else {
                    Log.e("Playlist Maker Debug", "HTTP ошибка: ${resp.code()} - ${resp.message()}")
                    NetResponse()
                }
            } catch (e: Exception) {
                Log.e("Playlist Maker Debug", "Неизвестная ошибка интернет-соединения: ${e.localizedMessage}")
                NetResponse()
            }
        } else {
            Log.e("Playlist Maker Debug", "В itunesNetworkClient.doRequest передан некорректный тип данных.")
            return NetResponse()
        }
    }

    companion object {
        const val ITUNES_BASE_URL = "https://itunes.apple.com"
    }
}