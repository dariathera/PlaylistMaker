package com.practicum.playlistmaker.player.ui.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.AddToPlaylistListItemBinding
import com.practicum.playlistmaker.library.domain.entities.PlaylistGeneralInformation
import com.practicum.playlistmaker.util.DrawingTools

class AddToPlaylistViewHolder(private val binding: AddToPlaylistListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private val roundRadiusDp: Float = 2f

    fun bind(model: PlaylistGeneralInformation) {
        binding.apply {
            name.text = model.name
            size.text = itemView.context.resources.getQuantityString(
                R.plurals.tracks_count,
                model.quantity,
                model.quantity
            )
        }
        Glide.with(itemView)
            .load(model.uri)
            .placeholder(R.drawable.ic_artwork_placeholder_45)
            .transform(
                CenterCrop(),
                RoundedCorners(
                    DrawingTools.dpToPx(
                        roundRadiusDp,
                        itemView.context
                    )
                )
            ).into(binding.playlistCover)
    }

    companion object {
        fun from(parent: ViewGroup): AddToPlaylistViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = AddToPlaylistListItemBinding.inflate(inflater, parent, false)
            return AddToPlaylistViewHolder(binding)
        }
    }
}