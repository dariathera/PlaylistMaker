package com.practicum.playlistmaker.data.lists.searchTrack

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.objects.Track

class SearchTrackAdapter (
    private val tracks: List<Track>
) : RecyclerView.Adapter<SearchTrackViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchTrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_track_list_item, parent, false)
        return SearchTrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchTrackViewHolder, position: Int) {
        holder.bind(tracks[position])

        holder.itemView.post {
            val artistName: TextView = holder.itemView.findViewById(R.id.artistName)

            val textViewWidth = artistName.width
            val textWidth = artistName.paint.measureText(artistName.text.toString())
            if (textViewWidth > textWidth) {
                artistName.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    0f
                )
                tracks[holder.adapterPosition].isFixed = true
            }
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onViewRecycled(holder: SearchTrackViewHolder) {
        super.onViewRecycled(holder)
        tracks[holder.adapterPosition].isFixed = false
    }

}

