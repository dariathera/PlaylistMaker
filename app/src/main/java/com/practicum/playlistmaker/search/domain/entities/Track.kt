package com.practicum.playlistmaker.search.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTime: Int, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val trackId: Long,
    val album: String?,
    val year: Int?,
    val genre: String?,
    val country: String?,
    val previewUrl: String?
): Parcelable {
    fun getHighArtworkUrl() : String {
        return artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
    }
}