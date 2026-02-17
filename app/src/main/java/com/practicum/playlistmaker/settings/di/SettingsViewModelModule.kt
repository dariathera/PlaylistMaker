package com.practicum.playlistmaker.settings.di

import com.practicum.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import android.content.Context
import org.koin.core.parameter.parametersOf

val settingsViewModelModule = module {
    viewModel { (context: Context) ->
        SettingsViewModel(
            get { parametersOf(context) },
            get()
        )
    }
}
