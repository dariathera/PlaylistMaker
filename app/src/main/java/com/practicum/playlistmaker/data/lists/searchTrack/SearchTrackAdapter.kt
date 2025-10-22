package com.practicum.playlistmaker.data.lists.searchTrack

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.objects.Track
import com.practicum.playlistmaker.data.saving.SearchHistorySaver
import com.practicum.playlistmaker.net.ItunesTrackData

class SearchTrackAdapter (
    private val tracks: MutableList<Track>
) : RecyclerView.Adapter<SearchTrackViewHolder> () {

    // private var myContext : Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchTrackViewHolder {
        // myContext = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_track_list_item, parent, false)
        return SearchTrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchTrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            SearchHistorySaver.save(tracks[position])

            Log.d("Playlist Maker Debug", "Нажатие на трек")
            /*
            if (myContext != null) {
                Toast.makeText(myContext, "Нажали на трек", Toast.LENGTH_LONG).show()
            }
            */

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

