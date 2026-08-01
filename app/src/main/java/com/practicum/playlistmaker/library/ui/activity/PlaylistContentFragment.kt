package com.practicum.playlistmaker.library.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistContentBinding
import com.practicum.playlistmaker.library.ui.viewmodel.PlaylistContentViewModel
import com.practicum.playlistmaker.player.ui.activity.AudioplayerFragment
import com.practicum.playlistmaker.root.ui.viewmodel.SharedViewModel
import com.practicum.playlistmaker.search.domain.entities.Track
import com.practicum.playlistmaker.search.ui.activity.SearchTrackAdapter
import com.practicum.playlistmaker.util.DrawingTools
import debounce
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class PlaylistContentFragment: Fragment() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val ARGS_PLAYLIST_ID = "playlist_id_key"
        private val roundRadiusDp: Float = 2f
        private var playlistName: String = ""

        fun createArgs(playlistId: Long): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlistId)
    }
    private var _binding: FragmentPlaylistContentBinding? = null
    private val binding get() = _binding!!
    // !!! При использовании viewBinding во фрагментах обязательно
    // нужно обнулять _binding в onDestroyView, иначе будет утечка памяти !!!

    private lateinit var viewModel: PlaylistContentViewModel
    private var trackAdapter : SearchTrackAdapter? = null
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private var screenHeight: Int = 0
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<NestedScrollView>
    private lateinit var tracksBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private val sharedViewModel: SharedViewModel by activityViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentPlaylistContentBinding.inflate(inflater, container, false)
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

        viewModel = getViewModel<PlaylistContentViewModel> {
            parametersOf(
                requireArguments().getLong(ARGS_PLAYLIST_ID),
                requireContext()
            )
        }

        // Кнопка назад
        binding.btnGoBack.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.observeForcedReturn().observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        trackAdapter = SearchTrackAdapter(
            ArrayDeque<Track>(),
            { track: Track ->
                onTrackClickDebounce(track)
            },
            { track: Track ->
                showDeleteTrackDialog(track)
            }
        )

        binding.bottomsheetPlaylistContent.recyclerView.adapter = trackAdapter

        viewModel.observeTrackList().observe(viewLifecycleOwner) {
            trackAdapter?.updateData(it)
        }

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            findNavController().navigate(
                R.id.action_playlistContentFragment_to_audioplayerFragment,
                AudioplayerFragment.createArgs(track)
            )
        }

        tracksBottomSheetBehavior =
            BottomSheetBehavior.from(binding.bottomsheetPlaylistContent.root).apply {
                isHideable = false
                screenHeight = resources.displayMetrics.heightPixels
                peekHeight = (screenHeight / 3).toInt()
                maxHeight = (screenHeight * 2 / 3).toInt()
                state = BottomSheetBehavior.STATE_COLLAPSED
            }


        menuBottomSheetBehavior =
            BottomSheetBehavior.from(binding.bottomsheetExtraMenu.root).apply {
                isHideable = true
                screenHeight = resources.displayMetrics.heightPixels
                peekHeight = (screenHeight / 3).toInt()
                state = BottomSheetBehavior.STATE_HIDDEN
            }

        binding.btnMore.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        menuBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.scrim.isVisible = false
                    }
                    else -> {
                        binding.scrim.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset > 0) {
                    binding.scrim.isVisible = true
                } else {
                    binding.scrim.isVisible = false
                }
            }

        })

        binding.scrim.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        viewModel.observePlaylistInfo().observe(viewLifecycleOwner) {
            playlistName = it.name
            binding.apply {
                // общая информация
                name.text = it.name
                if (it.description.isNotEmpty()) {
                    description.text = it.description
                    description.isVisible = true
                } else {
                    description.isVisible = false
                }
                generalDuration.text = context?.resources?.getQuantityString(
                    R.plurals.duration_in_minutes,
                    it.totalDuration,
                    it.totalDuration
                )
                size.text = context?.resources?.getQuantityString(
                    R.plurals.tracks_count,
                    it.numberOfTracks,
                    it.numberOfTracks
                )
                Glide.with(playlistCover)
                    .load(it.coverUri)
                    .placeholder(R.drawable.ic_artwork_placeholder_45)
                    .transform(
                        CenterCrop()
                    ).into(playlistCover)

                // дополнительное меню
                bottomsheetExtraMenu.playlistData.name.text = it.name
                bottomsheetExtraMenu.playlistData.size.text = context?.resources?.getQuantityString(
                    R.plurals.tracks_count,
                    it.numberOfTracks,
                    it.numberOfTracks
                )

                Glide.with(bottomsheetExtraMenu.playlistData.playlistCover)
                    .load(it.coverUri)
                    .placeholder(R.drawable.ic_artwork_placeholder_45)
                    .transform(
                        CenterCrop(),
                        RoundedCorners(
                            DrawingTools.dpToPx(
                                roundRadiusDp,
                                requireContext()
                            )
                        )
                    ).into(bottomsheetExtraMenu.playlistData.playlistCover)

            }
        }

        binding.btnShare.setOnClickListener {
            viewModel.sharePlaylist(sharedViewModel)
        }

        binding.bottomsheetExtraMenu.share.setOnClickListener {
            viewModel.sharePlaylist(sharedViewModel)
        }

        binding.bottomsheetExtraMenu.delete.setOnClickListener {
            showDeletePlaylistDialog()
        }

        binding.bottomsheetExtraMenu.edit.setOnClickListener {
            findNavController().navigate(
                R.id.action_playlistContentFragment_to_playlistFormFragment,
                PlaylistFormFragment.createArgs(
                    requireArguments().getLong(ARGS_PLAYLIST_ID)
                )
            )
        }

    }

    private fun showDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.do_you_want_to_delete_the_track))
            .setNegativeButton(getString(R.string.no)) { dialog, which ->
                // Ничего не делаем
            }.setPositiveButton(getString(R.string.yes)) { dialog, which ->
                viewModel.deleteTrackFromPlaylist(track.trackId)
            }.show()
    }

    private fun showDeletePlaylistDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.do_you_want_to_delete_the_playlist, playlistName))
            .setNegativeButton(getString(R.string.no)) { dialog, which ->
                // Ничего не делаем
            }.setPositiveButton(getString(R.string.yes)) { dialog, which ->
                viewModel.deletePlaylist()
                findNavController().navigateUp()
            }.show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

