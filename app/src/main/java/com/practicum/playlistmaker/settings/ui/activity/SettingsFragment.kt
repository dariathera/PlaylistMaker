package com.practicum.playlistmaker.settings.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: SettingsViewModel by viewModel {
        parametersOf(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настраиваем отступы
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        binding.shareTheApp.setOnClickListener {
            viewModel.shareApp()
        }

        binding.writeToSupport.setOnClickListener {
            viewModel.writeToSupport()
        }

        binding.userAgreement.setOnClickListener {
            viewModel.openUserAgreement()
        }

        viewModel.observeColorTheme().observe(viewLifecycleOwner) {
            Log.d("ColorTheme", "В обзёрвер пришло значение ${it.toString()}")
            binding.themeSwitcher.setChecked(it)
        }

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            Log.d("ColorTheme", "Зарегистрировано нажатие на свитчер ${checked.toString()}")
            viewModel.switchColorTheme(checked)
        }
    }
}