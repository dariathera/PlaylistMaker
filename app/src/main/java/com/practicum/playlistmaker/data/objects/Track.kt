package com.practicum.playlistmaker.data.objects

data class Track(
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTime: String, // Продолжительность трека, в макете duration
    val artworkUrl100: String, // Ссылка на изображение обложки
    val trackId: Int,
    val album: String?,
    val year: Int?,
    val genre: String?,
    val country: String?
) {
    fun getHighArtworkUrl() : String {
        return artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
    }
}

/*
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Track(
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTime: String, // Продолжительность трека, в макете duration
    val artworkUrl100: String, // Ссылка на изображение обложки
    val trackId: Int,
    val album: String,
    val year: Int,
    val genre: String,
    val country: String
) : Parcelable
*/