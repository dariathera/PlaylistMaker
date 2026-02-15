package com.practicum.playlistmaker.settings.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.practicum.playlistmaker.util.Creator

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getFactory(
                Creator.provideSharingInteractor(this),
                Creator.provideSettingsInteractor()
            )
        ).get(SettingsViewModel::class.java)

        binding.btnGoBack.setOnClickListener {
            finish() //Удаляет SettingsActivity из стека.
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

        viewModel.observeColorTheme().observe(this) {
            binding.themeSwitcher.setChecked(it)
        }

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.switchColorTheme(checked)
        }
    }
}