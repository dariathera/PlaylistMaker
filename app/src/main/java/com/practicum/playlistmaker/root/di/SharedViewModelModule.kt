package com.practicum.playlistmaker.root.di

import com.practicum.playlistmaker.root.ui.viewmodel.SharedViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val sharedViewModelModule = module {
    viewModel {
        SharedViewModel()
    }
}