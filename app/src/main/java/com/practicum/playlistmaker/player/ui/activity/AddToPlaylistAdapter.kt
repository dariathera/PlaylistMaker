package com.practicum.playlistmaker.player.ui.activity

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.library.domain.entities.PlaylistGeneralInformation
import org.koin.core.component.KoinComponent

class AddToPlaylistAdapter (
    private val onItemClick: (id: Long) -> Unit
) : RecyclerView.Adapter<AddToPlaylistViewHolder> (), KoinComponent {

    private val playlists: MutableList<PlaylistGeneralInformation> = mutableListOf()
    private var myContext : Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            AddToPlaylistViewHolder {
        myContext = parent.context
        return AddToPlaylistViewHolder.Companion.from(parent)
    }

    override fun onBindViewHolder(holder: AddToPlaylistViewHolder, position: Int) {

        val context = myContext

        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            if (context != null) {
                val playlist = playlists[position]
                onItemClick(playlist.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    fun updateData(newPlaylists: List<PlaylistGeneralInformation>) {
        playlists.clear()
        playlists.addAll(newPlaylists)
        notifyDataSetChanged()
    }
}