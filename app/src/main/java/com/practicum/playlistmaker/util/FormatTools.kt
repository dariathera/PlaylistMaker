package com.practicum.playlistmaker.util

import java.text.SimpleDateFormat
import java.util.Locale

object FormatTools {
    fun millisToMmss(millis: Int) : String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(millis)
    }
}