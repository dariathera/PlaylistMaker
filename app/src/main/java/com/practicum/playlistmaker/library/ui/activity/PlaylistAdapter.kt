package com.practicum.playlistmaker.library.ui.activity

import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.library.domain.PrivateStorageApi
import com.practicum.playlistmaker.library.domain.entities.PlaylistGeneralInformation
import com.practicum.playlistmaker.search_history.domain.GetHistoryInteractor
import com.practicum.playlistmaker.search.domain.entities.Track
import com.practicum.playlistmaker.search.ui.activity.SearchTrackViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class PlaylistAdapter (
    private val onItemClick: (id: Long) -> Unit
) : RecyclerView.Adapter<PlaylistViewHolder> (), KoinComponent {
    private val playlists: MutableList<PlaylistGeneralInformation> = mutableListOf()
    private var myContext : Context? = null
    // private lateinit var searchHistorySaver : GetHistoryInteractor
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
                /*searchHistorySaver = getKoin().get()
                managerScope.launch(Dispatchers.IO) {
                    searchHistorySaver.save(track)
                }

                 */
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