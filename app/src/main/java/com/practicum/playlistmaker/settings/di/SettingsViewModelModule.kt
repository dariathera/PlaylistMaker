package com.practicum.playlistmaker.settings.di

import android.content.Context
import com.practicum.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val settingsViewModelModule = module {
    viewModel { (context: Context) ->
        SettingsViewModel(
            get { parametersOf(context) },
            get()
        )
    }
}
