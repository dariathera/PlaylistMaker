package com.practicum.playlistmaker.player.ui.activity

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.library.domain.entities.PlaylistGeneralInformation
import com.practicum.playlistmaker.library.ui.activity.PlaylistViewHolder
import com.practicum.playlistmaker.search_history.domain.GetHistoryInteractor
import com.practicum.playlistmaker.search.domain.entities.Track
import com.practicum.playlistmaker.search.ui.activity.SearchTrackViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
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
        Log.d("Adapter", "itemCount = ${playlists.size}")
        return playlists.size
    }


    fun updateData(newPlaylists: List<PlaylistGeneralInformation>) {
        Log.d("Adapter", "updateData called with ${newPlaylists.size} items")
        playlists.clear()
        playlists.addAll(newPlaylists)
        notifyDataSetChanged()
    }
}