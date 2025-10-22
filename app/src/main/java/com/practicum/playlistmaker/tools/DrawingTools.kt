package com.practicum.playlistmaker.tools

import android.content.Context
import android.util.TypedValue

object DrawingTools {
    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}