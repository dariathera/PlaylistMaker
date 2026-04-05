package com.practicum.playlistmaker.player.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentAudioplayerBinding
import com.practicum.playlistmaker.player.ui.viewmodel.AudioplayerViewModel
import com.practicum.playlistmaker.search.domain.entities.Track
import com.practicum.playlistmaker.util.DrawingTools
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class AudioplayerFragment : Fragment() {

    companion object {
        private const val ARGS_TRACK = "track_key"

        fun createArgs(track: Track): Bundle =
            bundleOf(ARGS_TRACK to track)
    }
    private lateinit var binding: FragmentAudioplayerBinding
    private lateinit var viewModel: AudioplayerViewModel
    private val currentTrack: Track? by lazy(LazyThreadSafetyMode.NONE) {
        requireArguments().getParcelable<Track?>(ARGS_TRACK)
    }
    private val roundRadiusDp : Float = 8f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAudioplayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настраиваем отступы
        ViewCompat.setOnApplyWindowInsetsListener(binding.background) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val track = currentTrack

        if (track == null) {
            findNavController().navigateUp()
            return
        }

        viewModel = getViewModel<AudioplayerViewModel> {
            parametersOf(track.previewUrl)
        }

        // Кнопка назад
        binding.btnGoBack.setOnClickListener {
            findNavController().navigateUp()
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
        viewModel.observeIsPlaying().observe(viewLifecycleOwner) {
            if (it == true) {
                binding.playButton.setImageResource(R.drawable.ic_pause_512)
            } else {
                binding.playButton.setImageResource(R.drawable.ic_play_512)
            }
        }

        viewModel.observeTimeText().observe(viewLifecycleOwner) {
            binding.currentTime.text = it
        }

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        viewModel.observeShowMessage().observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    }

    override fun onPause() {
        super.onPause()
        if (requireActivity().isChangingConfigurations) {
            // не останавливаем воспроизведение при повороте
            return
        }
        viewModel.pausePlayer()
        viewModel.stopTimer()
    }

    private fun setText(s: String?) : String {
        return if (s == null) "" else s
    }

    private fun setText(s: Int?) : String {
        return if (s == null) "" else s.toString()
    }

}