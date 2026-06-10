package com.practicum.playlistmaker.search.data.client

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.search.data.dto.GetTracksRequest
import com.practicum.playlistmaker.search.data.dto.NetResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ItunesNetworkClient(
    private val context: Context,
    private val itunesService: ItunesApi
) : NetworkClient {

    override suspend fun doRequest(dto: Any): NetResponse {
        if (isConnected() == false) {
            return NetResponse.NetConnectionError
        }
        if (dto is GetTracksRequest) {
            return try {
                val itunesResponse = itunesService.search(dto.expression)
                if (itunesResponse != null && itunesResponse.results.isNotEmpty()) {
                    NetResponse.GoodNetResponse(200, ArrayList(itunesResponse.results))
                } else {
                    NetResponse.BlankResponse(200)
                }
            } catch (e: Throwable) {
                Log.e(App.Companion.ERROR_LOG_TAG, "Ошибка при выполнении HTTP запроса")
                NetResponse.ServerError(500)
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