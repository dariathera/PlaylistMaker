package com.practicum.playlistmaker.net

import android.util.Log
import com.practicum.playlistmaker.data.objects.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale

class NetworkInteracter {

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val itunesService = retrofit.create(ItunesApi::class.java)

    fun getMusic(
        text: String,
        callback: (SearchActivityViewMarker, MutableList<Track>?) -> Unit)
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
                            Log.d("getMusic", "${data.resultCount} tracks found")
                            if (data.resultCount == 0 || data.results.isEmpty()) {
                                callback(SearchActivityViewMarker.NOTHING_FOUND, null)
                            } else {
                                callback(
                                    SearchActivityViewMarker.GOOD_RESPONSE,
                                    data.results.map { result ->
                                        Track(
                                            trackName = result.trackName,
                                            artistName = result.artistName,
                                            trackTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(result.trackTimeMillis),
                                            artworkUrl100 = result.artworkUrl100
                                        )
                                    }.toMutableList()
                                )
                            }
                        } else {
                            Log.e("getMusic", "Server error: ${response.code()}")
                            callback(SearchActivityViewMarker.ERROR, null)
                        }
                    } else {
                        Log.e("getMusic", "Server error: ${response.code()}")
                        callback(SearchActivityViewMarker.ERROR, null)
                    }
                }

                override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                    Log.e("getMusic", "Network error", t)
                    callback(SearchActivityViewMarker.ERROR, null)
                }
            })
    }
}
