package com.practicum.playlistmaker.player.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioplayerBinding
import com.practicum.playlistmaker.search.domain.entities.Track
import com.practicum.playlistmaker.util.DrawingTools
import com.practicum.playlistmaker.player.ui.viewmodel.AudioplayerViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue

class AudioplayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioplayerBinding
    private lateinit var viewModel: AudioplayerViewModel

    private val currentTrack: Track? by lazy(LazyThreadSafetyMode.NONE) {
        intent.getParcelableExtra("track_key")
    }

    private val roundRadiusDp : Float = 8f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val track = currentTrack

        if (track == null) {
            finish()
            return
        }

        viewModel = getViewModel<AudioplayerViewModel> {
            parametersOf(track.previewUrl)
        }

        enableEdgeToEdge()
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Кнопка назад
        binding.btnGoBack.setOnClickListener {
            finish()
        }

        // Отображаем информацию о треке
        // Использовать новую версию метода не позволяет minSdkVersion = 29
        binding.apply {
            trackName.text =  setText(track.trackName)
            artistName.text = setText(track.artistName)
            duration.text = setText(track.trackTime)
            album.text = setText(track.album)
            year.text = setText(track.year)
            genre.text = setText(track.genre)
            country.text = setText(track.country)
        }

        Glide.with(binding.artwork)
            .load(track.getHighArtworkUrl())
            .placeholder(R.drawable.ic_artwork_placeholder_45)
            .centerCrop()
            .transform(
                RoundedCorners(
                    DrawingTools.dpToPx(
                        roundRadiusDp,
                        binding.artwork.context
                    )
                )
            )
            .into(binding.artwork)

        // Управление воспроизведением
        viewModel.observeIsPlaying().observe(this) {
            if (it == true) {
                binding.playButton.setImageResource(R.drawable.ic_pause_512)
            } else {
                binding.playButton.setImageResource(R.drawable.ic_play_512)
            }
        }

        viewModel.observeTimeText().observe(this) {
            binding.currentTime.text = it
        }

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        viewModel.observeShowMessage().observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }
    }

    override fun onPause() {
        super.onPause()
        if (isChangingConfigurations) {
            // не останавливаем воспроизведение при повороте
            return
        }
        viewModel.pausePlayer()
        viewModel.stopTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun setText(s: String?) : String {
        return if (s == null) "" else s
    }

    private fun setText(s: Int?) : String {
        return if (s == null) "" else s.toString()
    }
}