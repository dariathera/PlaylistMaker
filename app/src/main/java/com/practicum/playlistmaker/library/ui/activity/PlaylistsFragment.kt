package com.practicum.playlistmaker.library.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.library.ui.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class PlaylistsFragment : Fragment() {

    private val viewModel: PlaylistsViewModel by viewModel()

    companion object {
        // private const val NUMBER = "number"

        fun newInstance(number: Int) = PlaylistsFragment().apply {
            arguments = Bundle().apply {
                // putInt(NUMBER, number)
            }
        }
    }

    private lateinit var binding: FragmentPlaylistsBinding

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
                }
                else -> {
                    binding.textView.visibility = View.GONE
                    binding.imageView.visibility = View.GONE
                }
            }
        }
    }

}