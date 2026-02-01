package com.practicum.playlistmaker.main.ui.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.library.ui.activity.LibraryActivity
import com.practicum.playlistmaker.search.ui.activity.SearchActivity
import com.practicum.playlistmaker.settings.ui.activity.SettingsActivity

class MainViewModel(private val context: Context): ViewModel() {

    fun goLibrary() {
        go(LibraryActivity::class.java)
    }

    fun goSetting() {
        go(SettingsActivity::class.java)
    }

    fun goSearch() {
        go(SearchActivity::class.java)
    }

    private fun go(cls : Class<*>) {
        val displayIntent = Intent(context, cls)
        context.startActivity(displayIntent)
    }

    override fun onCleared() {
        super.onCleared()
    }

    companion object {
        fun getFactory(
            context: Context
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MainViewModel(context)
            }
        }
    }
}