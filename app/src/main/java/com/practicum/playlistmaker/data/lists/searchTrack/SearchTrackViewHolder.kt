package com.practicum.playlistmaker.data.lists.searchTrack

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.objects.Track
import com.practicum.playlistmaker.tools.DrawingTools


class SearchTrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
    private val artwork: ImageView = itemView.findViewById(R.id.artwork)
    private val roundRadiusDp: Float = 2f

    fun bind(model: Track) {
        trackName.text = model.trackName
        trackTime.text = model.trackTime
        artistName.text = model.artistName
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.ic_artwork_placeholder_45)
            .centerCrop()
            .transform(RoundedCorners(DrawingTools.dpToPx(roundRadiusDp, itemView.context)))
            .into(artwork)
    }

}



