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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentAudioplayerBinding
import com.practicum.playlistmaker.library.domain.entities.PlaylistGeneralInformation
import com.practicum.playlistmaker.library.ui.activity.PlaylistFormFragment
import com.practicum.playlistmaker.player.ui.viewmodel.AudioplayerViewModel
import com.practicum.playlistmaker.root.ui.viewmodel.SharedViewModel
import com.practicum.playlistmaker.search.domain.entities.Track
import com.practicum.playlistmaker.util.DrawingTools
import com.practicum.playlistmaker.util.FormatTools
import debounce
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class AudioplayerFragment : Fragment() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
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
    private var playlistAdapter : AddToPlaylistAdapter? = null
    private lateinit var onClickDebounce: (Unit) -> Unit
    private var screenHeight: Int = 0
    private val sharedViewModel: SharedViewModel by activityViewModel()


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
            parametersOf(track)
        }

        // Кнопка назад
        binding.btnGoBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Отображаем информацию о треке
        // Использовать новую версию метода не позволяет minSdkVersion = 29
        binding.apply {
            trackName.text = setText(track.trackName)
            artistName.text = setText(track.artistName)
            duration.text = setText(FormatTools.millisToMmss(track.trackTime))
            album.text = setText(track.album)
            year.text = setText(track.year)
            genre.text = setText(track.genre)
            country.text = setText(track.country)
        }

        Glide.with(binding.artwork)
            .load(track.getHighArtworkUrl())
            .placeholder(R.drawable.ic_artwork_placeholder_45)
            .transform(
                CenterCrop(),
                RoundedCorners(
                    DrawingTools.dpToPx(
                        roundRadiusDp,
                        requireContext()
                    )
                )
            ).into(binding.artwork)

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

        binding.btnLike.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        viewModel.observeIsFavorite().observe(viewLifecycleOwner) {
            if (it == true) {
                binding.btnLike.setImageResource(R.drawable.ic_filled_heart_250)
            } else {
                binding.btnLike.setImageResource(R.drawable.ic_blank_heart_250)
            }
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomsheet.root).apply {
            isHideable = true          // разрешаем скрытие
            halfExpandedRatio = 0.5f   // Для высоты экрана для состояния HALF_EXPANDED (0.5 = половина)
            peekHeight = 0             // в свёрнутом состоянии высота 0
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.btnPlaylist.setOnClickListener {
            onClickDebounce(Unit)
            playlistAdapter?.updateData(listOf<PlaylistGeneralInformation>())
            val targetHeight = (screenHeight / 3).toInt()
            bottomSheetBehavior.peekHeight = targetHeight
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            viewModel.requestPlaylists()
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.scrim.visibility = View.GONE
                    }
                    else -> {
                        val targetHeight = (screenHeight * 2 / 3).toInt()
                        binding.bottomsheet.recyclerView.layoutParams.height = targetHeight
                        binding.scrim.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset > 0) {
                    binding.scrim.visibility = View.VISIBLE
                } else {
                    binding.scrim.visibility = View.GONE
                }
            }
        })

        binding.scrim.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        onClickDebounce = debounce<Unit>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) {}

        playlistAdapter = AddToPlaylistAdapter(
            {id: Long ->
                viewModel.handleClickOnPlaylist(id, sharedViewModel)
                onClickDebounce(Unit)
            }
        )

        binding.bottomsheet.recyclerView.adapter = playlistAdapter
        binding.bottomsheet.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.observePlaylists().observe(viewLifecycleOwner){
            playlistAdapter?.updateData(it)
        }

        viewModel.observeHideBottomSheet().observe(viewLifecycleOwner){
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.bottomsheet.btnNewPlaylist.setOnClickListener { it ->
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            findNavController().navigate(
                R.id.action_audioplayerFragment_to_playlistFormFragment,
                PlaylistFormFragment.createArgs()
            )
        }
    }

    override fun onResume() {
        super.onResume()
        // Всё это нужно для установки высоты списка внутри bottomsheet
        // В xml ее настроить не получилось
        val displayMetrics = resources.displayMetrics
        screenHeight = displayMetrics.heightPixels
        viewModel.requestPlaylists()
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