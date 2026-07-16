package com.practicum.playlistmaker.library.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.library.ui.viewmodel.PlaylistsViewModel
import debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class PlaylistsFragment : Fragment() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        fun newInstance(number: Int) = PlaylistsFragment().apply {
            arguments = Bundle().apply {}
        }
    }

    private val viewModel: PlaylistsViewModel by viewModel()
    private lateinit var binding: FragmentPlaylistsBinding
    private var playlistAdapter : PlaylistAdapter? = null
    private lateinit var onPlaylistClickDebounce: (Long) -> Unit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) {
            when(it) {
                PlaylistsState.NoPlaylists -> {
                    binding.textView.visibility = View.VISIBLE
                    binding.imageView.visibility = View.VISIBLE
                    binding.recyclerView?.visibility = View.VISIBLE
                }
                is PlaylistsState.UserPlaylists -> {
                    playlistAdapter?.updateData(it.playlists)
                    binding.textView.visibility = View.GONE
                    binding.imageView.visibility = View.GONE
                    binding.recyclerView?.visibility = View.VISIBLE
                }
                else -> {
                    binding.textView.visibility = View.GONE
                    binding.imageView.visibility = View.GONE
                    binding.recyclerView?.visibility = View.GONE
                }
            }
        }

        binding.btnNewPlaylist.setOnClickListener { it ->
            findNavController().navigate(
                R.id.action_libraryFragment_to_playlistCreatorFragment
            )
        }

        val spanCount = 2
        val orientation = RecyclerView.VERTICAL // или HORIZONTAL
        val reverseLayout = false // порядок заполнения: false — слева направо, сверху вниз; true — наоборот

        val layoutManager = GridLayoutManager(requireContext(), spanCount, orientation, reverseLayout)
        binding.recyclerView?.layoutManager = layoutManager

        playlistAdapter = PlaylistAdapter(
            {id: Long ->
                Log.d(App.DEBUG_LOG_TAG, "PlaylistsFragment: пользователь нажал на плейлист с id $id")
                // Здесь должна быть логика нажатия на плейлист
                onPlaylistClickDebounce(id)
            }
        )

        binding.recyclerView?.adapter = playlistAdapter

        onPlaylistClickDebounce = debounce<Long>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) {}
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateState()
    }

}