package com.practicum.playlistmaker.data.lists.searchTrack

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.objects.Track

class SearchTrackAdapter (
    private val tracks: List<Track>
) : RecyclerView.Adapter<SearchTrackViewHolder> () {

    var setArtistNameListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchTrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_track_list_item, parent, false)
        return SearchTrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchTrackViewHolder, position: Int) {
        holder.bind(tracks[position])

        val artistName: TextView = holder.itemView.findViewById(R.id.artistName)

        setArtistNameListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                artistName.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val textViewWidth = artistName.width
                val textWidth = artistName.paint.measureText(artistName.text.toString())
                if (textViewWidth > textWidth) {
                    artistName.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        0f
                    )
                    tracks[holder.adapterPosition].isFixed = true
                    holder.itemView.post {
                        notifyItemChanged(position)
                    }
                }
            }
        }
        artistName.viewTreeObserver.addOnGlobalLayoutListener(setArtistNameListener)


    }

    override fun getItemCount(): Int {
        return tracks.size
    }

}

