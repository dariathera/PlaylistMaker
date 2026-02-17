package com.practicum.playlistmaker.search.data.client

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.search.data.dto.GetTracksRequest
import com.practicum.playlistmaker.search.data.dto.NetResponse

class ItunesNetworkClient(
    private val context: Context,
    private val itunesService: ItunesApi
) : NetworkClient {

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
        val connectivityManager: ConnectivityManager = getConnectivityManager()
        val capabilities: NetworkCapabilities? = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }

    private fun getConnectivityManager(): ConnectivityManager {
        // для Android 6.0+
        return context.getSystemService(ConnectivityManager::class.java)
    }
}