package com.practicum.playlistmaker.net

import com.practicum.playlistmaker.data.objects.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale

class NetworkInteracter {

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val itunesService = retrofit.create(ItunesApi::class.java)

    fun getMusic(
        text: String,
        callback: (GetMusicResponse) -> Unit)
    {

        itunesService
            .search(text)
            .enqueue(object : Callback<ItunesResponse> {
                override fun onResponse(call: Call<ItunesResponse>,
                                        response: Response<ItunesResponse>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null) {
                            if (data.resultCount == 0 || data.results.isEmpty()) {
                                callback(EmptyResponse)
                            } else {
                                callback(
                                    GoodResponse(
                                        data.results.map { result ->
                                            Track(
                                                trackName = result.trackName,
                                                artistName = result.artistName,
                                                trackTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(result.trackTimeMillis),
                                                artworkUrl100 = result.artworkUrl100,
                                                trackId = result.trackId
                                            )
                                        }.toMutableList()
                                    )
                                )
                            }
                        } else {
                            callback(BadResponse)
                        }
                    } else {
                        callback(BadResponse)
                    }
                }

                override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                    callback(BadResponse)
                }
            })
    }

    companion object {
        const val ITUNES_BASE_URL = "https://itunes.apple.com"
    }
}
