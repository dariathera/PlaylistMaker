package com.practicum.playlistmaker.library.ui.activity

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.library.domain.entities.PlaylistGeneralInformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.component.KoinComponent

class PlaylistAdapter (
    private val onItemClick: (id: Long) -> Unit
) : RecyclerView.Adapter<PlaylistViewHolder> (), KoinComponent {
    private val playlists: MutableList<PlaylistGeneralInformation> = mutableListOf()
    private var myContext : Context? = null
    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            PlaylistViewHolder {
        myContext = parent.context
        return PlaylistViewHolder.Companion.from(parent)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {

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