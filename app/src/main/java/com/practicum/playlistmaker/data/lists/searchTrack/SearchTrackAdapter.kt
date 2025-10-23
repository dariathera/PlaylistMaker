package com.practicum.playlistmaker.data.lists.searchTrack

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.activities.AudioplayerActivity
import com.practicum.playlistmaker.activities.LibraryActivity
import com.practicum.playlistmaker.data.objects.Track
import com.practicum.playlistmaker.data.saving.SearchHistorySaver
import com.practicum.playlistmaker.net.ItunesTrackData

class SearchTrackAdapter (
    private val tracks: MutableList<Track>
) : RecyclerView.Adapter<SearchTrackViewHolder> () {

    private var myContext : Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchTrackViewHolder {
        myContext = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_track_list_item, parent, false)
        return SearchTrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchTrackViewHolder, position: Int) {

        val context = myContext

        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {

            val track = tracks[position]

            SearchHistorySaver.save(track)
            Log.d("Playlist Maker Debug", "Нажатие на трек")

            if (context != null) {
                val displayIntent = Intent(context, AudioplayerActivity::class.java)
                displayIntent.putExtra("track_key", track)
                context.startActivity(displayIntent)
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

