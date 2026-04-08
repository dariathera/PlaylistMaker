package com.practicum.playlistmaker.settings.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    private val colorThemeLiveData = MutableLiveData<Boolean>(
        settingsInteractor.getColorTheme())
    fun observeColorTheme(): LiveData<Boolean> {
        Log.d("ColorTheme", "observeColorTheme вернул ${colorThemeLiveData.value.toString()}")
        return colorThemeLiveData
    }

    fun switchColorTheme(colorTheme: Boolean) {

        Log.d("ColorTheme", "Передано значение ${colorTheme.toString()}")
        settingsInteractor.switchColorTheme(colorTheme)
        colorThemeLiveData.postValue(
            // colorTheme
            settingsInteractor.getColorTheme()
        )
        Log.d("ColorTheme", "Установлено значение ${settingsInteractor.getColorTheme()}")
    }

    fun openUserAgreement() {
        sharingInteractor.openTerms()
    }

    fun writeToSupport() {
        sharingInteractor.openSupport()
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    override fun onCleared() {
        super.onCleared()
    }
}
