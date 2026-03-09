package com.practicum.playlistmaker.library.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentFavoritesBinding
import com.practicum.playlistmaker.library.ui.viewmodel.FavoritesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private val viewModel: FavoritesViewModel by viewModel()

    companion object {
        // private const val NUMBER = "number"

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

        viewModel.observeState().observe(viewLifecycleOwner) {
            when(it) {
                FavoritesState.NoFavoritesTracks -> {
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