package com.practicum.playlistmaker.library.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoritesBinding
import com.practicum.playlistmaker.library.ui.viewmodel.FavoritesViewModel
import com.practicum.playlistmaker.player.ui.activity.AudioplayerFragment
import com.practicum.playlistmaker.root.ui.activity.RootActivity
import com.practicum.playlistmaker.search.domain.entities.Track
import com.practicum.playlistmaker.search.ui.activity.SearchTrackAdapter
import debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private var favoriteTrackAdapter : SearchTrackAdapter? = null
    private val viewModel: FavoritesViewModel by viewModel()
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        fun newInstance(number: Int) = FavoritesFragment().apply {
            arguments = Bundle().apply {
                // putInt(NUMBER, number)
            }
        }
    }

    private lateinit var binding: FragmentFavoritesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoriteTrackAdapter = SearchTrackAdapter(
            ArrayDeque<Track>(),
            {track: Track ->
                (activity as RootActivity).animateBottomNavigationView()
                onTrackClickDebounce(track)
            }
        )

        binding.recyclerView.adapter = favoriteTrackAdapter

        viewModel.observeState().observe(viewLifecycleOwner) {
            when(it) {
                FavoritesState.NoFavoritesTracks -> {
                    binding.textView.visibility = View.VISIBLE
                    binding.imageView.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                }
                is FavoritesState.FavoritesTracks -> {
                    favoriteTrackAdapter?.updateData(it.tracks)
                    binding.textView.visibility = View.GONE
                    binding.imageView.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                }
                FavoritesState.Loading -> {
                    binding.textView.visibility = View.GONE
                    binding.imageView.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                }
            }
        }

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            findNavController().navigate(
                R.id.action_libraryFragment_to_audioplayerFragment,
                AudioplayerFragment.createArgs(track)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateState()
    }
}