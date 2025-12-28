package com.practicum.playlistmaker.ui.audioplayer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
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
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.entities.Track
import com.practicum.playlistmaker.presentation.DrawingTools
import com.practicum.playlistmaker.presentation.MediaplayerState
import com.practicum.playlistmaker.presentation.TimerManager

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
    private lateinit var playButton : ImageButton
    private lateinit var mediaPlayer : MediaPlayer
    private var playerState : MediaplayerState = MediaplayerState.DEFAULT
    private var trackExists = false
    private lateinit var handler : Handler
    private lateinit var timerManager : TimerManager


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
        playButton = findViewById(R.id.btnPlayPause)
        mediaPlayer = MediaPlayer()
        timerManager = Creator.provideTimerManager(txtCurrentTime, mediaPlayer)
        handler = Handler(Looper.getMainLooper())

        playButton.setImageResource(R.drawable.ic_play_512)

        // Использовать новую версию метода не позволяет minSdkVersion = 29
        val track: Track? = intent.getParcelableExtra("track_key")
        track?.let {
            trackExists = true

            txtTrackName.text =  setText(track.trackName)
            txtArtistName.text = setText(track.artistName)
            txtDuration.text = setText(track.trackTime)
            txtAlbum.text = setText(track.album)
            txtYear.text = setText(track.year)
            txtGenre.text = setText(track.genre)
            txtCountry.text = setText(track.country)

            Glide.with(imgArtwork)
                .load(track.getHighArtworkUrl())
                .placeholder(R.drawable.ic_artwork_placeholder_45)
                .centerCrop()
                .transform(RoundedCorners(DrawingTools.dpToPx(roundRadiusDp, imgArtwork.context)))
                .into(imgArtwork)
        }

        // Управление воспроизведением
        preparePlayer()
        playButton.setOnClickListener {
            playbackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
        handler.removeCallbacks(timerManager.timerRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        timerManager.clearHandler()
    }

    private fun setText(s: String?) : String {
        return if (s == null) "" else s
    }

    private fun setText(s: Int?) : String {
        return if (s == null) "" else s.toString()
    }

    // Управление воспроизведением
    private fun preparePlayer() {
        val track: Track? = intent.getParcelableExtra("track_key")
        track?.let {
            mediaPlayer.setDataSource(track.previewUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playerState = MediaplayerState.PREPARED
                playButton.setImageResource(R.drawable.ic_play_512)
            }
            mediaPlayer.setOnCompletionListener {
                playButton.setImageResource(R.drawable.ic_play_512)
                playerState = MediaplayerState.PREPARED
                txtCurrentTime.text = timerManager.START_TIME_TEXT
            }
        }
    }

    private fun startPlayer() {
        if (trackExists) {
            mediaPlayer.start()
            playerState = MediaplayerState.PLAYING
            playButton.setImageResource(R.drawable.ic_pause_512)
        }
    }

    private fun pausePlayer() {
        if (trackExists) {
            mediaPlayer.pause()
            playerState = MediaplayerState.PAUSED
            playButton.setImageResource(R.drawable.ic_play_512)
        }
    }

    private fun playbackControl() {
        when(playerState) {
            MediaplayerState.PLAYING -> {
                pausePlayer()
                timerManager.stopTimer()
            }
            MediaplayerState.PREPARED, MediaplayerState.PAUSED -> {
                startPlayer()
                timerManager.startTimer()
            }
            MediaplayerState.DEFAULT -> {
                Log.e(App.ERROR_LOG_TAG, "Недопустимая ситуация: реализуется " +
                        "ветка DEFAULT в функции playbackControl(). Это значит, что ранее по " +
                        "какой-то причине функция preparePlayer() не была вызвана.")
            }
        }
    }
}