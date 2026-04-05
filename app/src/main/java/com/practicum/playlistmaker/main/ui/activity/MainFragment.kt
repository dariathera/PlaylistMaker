package com.practicum.playlistmaker.main.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настраиваем отступы
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // логика
        binding.btnSearch.setOnClickListener {
            // findNavController().navigate(R.id.action_mainFragment_to_searchFragment)
        }

        binding.btnSettings.setOnClickListener {
            // findNavController().navigate(R.id.action_mainFragment_to_settingsFragment)
        }

        binding.btnLibrary.setOnClickListener {
            // findNavController().navigate(R.id.action_mainFragment_to_libraryFragment)
        }
    }

}