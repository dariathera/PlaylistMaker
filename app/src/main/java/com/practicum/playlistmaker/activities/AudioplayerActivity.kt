package com.practicum.playlistmaker.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.objects.Track
import com.practicum.playlistmaker.data.saving.SearchHistorySaver
import com.practicum.playlistmaker.tools.DrawingTools

class AudioplayerActivity : AppCompatActivity() {
    private lateinit var btnGoBack: Button

    private lateinit var txtTrackName : TextView
    private lateinit var txtArtistName : TextView
    private lateinit var txtDuration : TextView
    private lateinit var txtAlbum : TextView
    private lateinit var txtYear : TextView
    private lateinit var txtGenre : TextView
    private lateinit var txtCountry : TextView
    private lateinit var imgArtwork : ImageView
    private lateinit var txtCurrentTime : TextView


    private val roundRadiusDp: Float = 8f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audioplayer)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Кнопка назад
        btnGoBack = findViewById(R.id.btnGoBack)
        btnGoBack.setOnClickListener {
            finish()
        }

        // Отображаем информацию о треке
        txtTrackName = findViewById(R.id.trackName)
        txtArtistName = findViewById(R.id.artistName)
        txtDuration = findViewById(R.id.duration)
        txtAlbum = findViewById(R.id.album)
        txtYear = findViewById(R.id.year)
        txtGenre = findViewById(R.id.genre)
        txtCountry = findViewById(R.id.country)
        imgArtwork = findViewById(R.id.artwork)
        txtCurrentTime = findViewById(R.id.currentTime)

        // val track: Track? = intent.getParcelableExtra("track_key")
        val track: Track? = SearchHistorySaver.getLastTrack()
        track?.let {

            txtTrackName.text =  setText(track.trackName)
            txtArtistName.text = setText(track.artistName)
            txtDuration.text = setText(track.trackTime)
            txtAlbum.text = setText(track.album)
            txtYear.text = setText(track.year)
            txtGenre.text = setText(track.genre)
            txtCountry.text = setText(track.country)

            // Заменить, когда будет написана логика воспроизведения
            txtCurrentTime.text = "0:00"

            Glide.with(imgArtwork)
                .load(track.getHighArtworkUrl())
                .placeholder(R.drawable.ic_artwork_placeholder_512)
                .centerCrop()
                .transform(RoundedCorners(DrawingTools.dpToPx(roundRadiusDp, imgArtwork.context)))
                .into(imgArtwork)
        }


    }

    private fun setText(s: String?) : String {
        return if (s == null) "" else s
    }

    private fun setText(s: Int?) : String {
        return if (s == null) "" else s.toString()
    }

    override fun onPause() {
        super.onPause()
        val app = App.getInstance()
        if (app != null) {
            val sharedPrefs: SharedPreferences = app.getSharedPreferences(
                app.USER_SETTINGS_PREFERENCES, MODE_PRIVATE
            )
            sharedPrefs.edit().putString(app.LAST_SCREEN_KEY, "audioplayer").apply()
        }
    }
}