package com.practicum.playlistmaker.db.data

import android.net.Uri
import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.domain.entities.Track

class Converters {

    @TypeConverter
    fun fromUri(uri: Uri?): String? {
        Log.d("DataB", "TypeConverter")
        Log.d("DataB", "uri: $uri")
        return uri?.toString()
    }

    @TypeConverter
    fun toUri(uriString: String?): Uri? {
        Log.d("DataB", "TypeConverter")
        Log.d("DataB", "uriString: ${uriString?.let { Uri.parse(it) }}")
        return uriString?.let { Uri.parse(it) }
    }

    @TypeConverter
    fun fromTrackList(tracks: MutableList<Track>?): String? {
        return if (tracks == null) null
        else Gson().toJson(tracks)
    }

    @TypeConverter
    fun toTrackList(json: String?): MutableList<Track>? {
        if (json == null) return null
        val type = object : TypeToken<MutableList<Track>>() {}.type
        return Gson().fromJson(json, type)
    }
}