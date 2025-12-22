package com.practicum.playlistmaker.ui.search

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.entities.Track
import com.practicum.playlistmaker.domain.repository.SearchHistorySaverRepository
import com.practicum.playlistmaker.ui.audioplayer.AudioplayerActivity
import com.practicum.playlistmaker.ui.search.SearchTrackViewHolder

class SearchTrackAdapter (
    private val tracks: MutableList<Track>,
    private val listener: OnTrackListClickListener? = null
) : RecyclerView.Adapter<SearchTrackViewHolder> () {

    private var myContext : Context? = null
    private lateinit var searchHistorySaver : SearchHistorySaverRepository

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
            searchHistorySaver = Creator.getSearchHistorySaverRepository()
            searchHistorySaver.save(track)

            if (context != null && listener != null) {
               if (listener.clickDebounce()) {
                   // Log.d("Playlist Maker Debug", "Нажатие на трек обработано")
                   val displayIntent = Intent(context, AudioplayerActivity::class.java)
                   displayIntent.putExtra("track_key", track)
                   context.startActivity(displayIntent)
               } else {
                   // Log.d("Playlist Maker Debug", "Нажатие на трек заблокировано дебаунсером")
               }
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