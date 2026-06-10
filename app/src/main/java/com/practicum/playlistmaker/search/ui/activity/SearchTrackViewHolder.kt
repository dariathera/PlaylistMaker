package com.practicum.playlistmaker.search.ui.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.SearchTrackListItemBinding
import com.practicum.playlistmaker.search.domain.entities.Track
import com.practicum.playlistmaker.util.DrawingTools
import com.practicum.playlistmaker.util.FormatTools

class SearchTrackViewHolder(private val binding: SearchTrackListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private val roundRadiusDp: Float = 2f

    fun bind(model: Track) {
        binding.apply {
            trackName.text = model.trackName
            trackTime.text = FormatTools.millisToMmss(model.trackTime)
            artistName.text = model.artistName
        }
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.ic_artwork_placeholder_45)
            .centerCrop()
            .transform(RoundedCorners(DrawingTools.dpToPx(roundRadiusDp, itemView.context)))
            .into(binding.artwork)
    }

    companion object {
        fun from(parent: ViewGroup): SearchTrackViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = SearchTrackListItemBinding.inflate(inflater, parent, false)
            return SearchTrackViewHolder(binding)
        }
    }
}