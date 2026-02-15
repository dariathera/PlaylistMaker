package com.practicum.playlistmaker.search.data.client

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.search.data.dto.GetTracksRequest
import com.practicum.playlistmaker.search.data.dto.NetResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ItunesNetworkClient() : NetworkClient {

    private val context = App.getContext()

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val itunesService = retrofit.create(ItunesApi::class.java)

    override fun doRequest(dto: Any): NetResponse {
        if (isConnected() == false) {
            return NetResponse.NetConnectionError
        }
        if (dto is GetTracksRequest) {
            return try {
                val resp = itunesService.search(dto.expression).execute()

                if (resp.isSuccessful) {
                    val itunesResponse = resp.body()
                    if (itunesResponse != null && itunesResponse.results.isNotEmpty()) {
                        NetResponse.GoodNetResponse(resp.code(), ArrayList(itunesResponse.results))
                    } else {
                        NetResponse.BlankResponse(resp.code())
                    }
                } else {
                    Log.e(App.Companion.ERROR_LOG_TAG, "HTTP ошибка: ${resp.code()} - ${resp.message()}")
                    NetResponse.ServerError(resp.code())
                }
            } catch (e: Exception) {
                Log.e(App.Companion.ERROR_LOG_TAG, "Неизвестная ошибка при выполнении запроса в ItunesNetworkClient: ${e.localizedMessage}")
                NetResponse.UnknownError
            }
        } else {
            Log.e(App.Companion.ERROR_LOG_TAG, "В itunesNetworkClient.doRequest передан некорректный тип данных.")
            return NetResponse.UnknownError
        }
    }

    //--------------------------------------------------------------------------------------
    // Проверка подключения к сети
    private fun isConnected(): Boolean {
        val connectivityManager: ConnectivityManager? = getConnectivityManager(context)
        val capabilities: NetworkCapabilities? = connectivityManager?.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }

    @Suppress("DEPRECATION")
    private fun getConnectivityManager(context: Context): ConnectivityManager? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Для Android API 23+ (Android 6.0+). У нас API 31.
            context.getSystemService(ConnectivityManager::class.java)
        } else {
            // Для старых версий ОС
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        }
    }
    //--------------------------------------------------------------------------------------

    companion object {
        const val ITUNES_BASE_URL = "https://itunes.apple.com"
    }
}