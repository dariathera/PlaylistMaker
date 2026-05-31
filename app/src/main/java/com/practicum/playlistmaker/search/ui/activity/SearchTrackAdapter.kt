package com.practicum.playlistmaker.search.ui.activity

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.search_history.domain.GetHistoryInteractor
import com.practicum.playlistmaker.search.domain.entities.Track
import org.koin.core.component.KoinComponent

class SearchTrackAdapter (
    private val tracks: MutableList<Track>,
    private val onItemClick: (track: Track) -> Unit
) : RecyclerView.Adapter<SearchTrackViewHolder> (), KoinComponent {

    private var myContext : Context? = null
    private lateinit var searchHistorySaver : GetHistoryInteractor

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            SearchTrackViewHolder {
        myContext = parent.context
        return SearchTrackViewHolder.Companion.from(parent)
    }

    override fun onBindViewHolder(holder: SearchTrackViewHolder, position: Int) {

        val context = myContext

        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            if (context != null) {
                val track = tracks[position]
                searchHistorySaver = getKoin().get()
                searchHistorySaver.save(track)
                onItemClick(track)
            }
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    fun updateData(newTracks: MutableList<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        notifyDataSetChanged()
    }
}