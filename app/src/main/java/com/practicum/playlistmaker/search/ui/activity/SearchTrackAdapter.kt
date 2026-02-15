package com.practicum.playlistmaker.search.ui.activity

import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.player.ui.activity.AudioplayerActivity
import com.practicum.playlistmaker.util.Creator
import com.practicum.playlistmaker.search_history.domain.GetHistoryInteractor
import com.practicum.playlistmaker.search.domain.entities.Track

class SearchTrackAdapter (
    private val tracks: MutableList<Track>,
    private val listener: OnTrackListClickListener? = null
) : RecyclerView.Adapter<SearchTrackViewHolder> () {

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
            if (context != null && listener != null) {
               if (listener.
                   clickDebounce()) {
                   val track = tracks[position]
                   searchHistorySaver = Creator.provideGetHistoryInteractor()
                   searchHistorySaver.save(track)
                   val displayIntent = Intent(context, AudioplayerActivity::class.java)
                   displayIntent.putExtra("track_key", track)
                   context.startActivity(displayIntent)
               } else {
                   // Нажатие на трек заблокировано дебаунсером
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