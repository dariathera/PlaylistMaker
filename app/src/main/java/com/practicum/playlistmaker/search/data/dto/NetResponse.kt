package com.practicum.playlistmaker.search.data.dto

sealed class NetResponse(open val resultCode: Int) {
    class GoodNetResponse (override val resultCode: Int,
                           val results: ArrayList<ItunesTrackData>) : NetResponse(resultCode)
    class BlankResponse(override val resultCode: Int) : NetResponse(resultCode)
    object NetConnectionError : NetResponse(-1)
    class ServerError(override val resultCode: Int) : NetResponse(resultCode)
    object UnknownError : NetResponse(-1)
}