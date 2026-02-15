package com.practicum.playlistmaker.settings.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    private val colorThemeLiveData = MutableLiveData<Boolean>(
        settingsInteractor.getColorTheme())
    fun observeColorTheme(): LiveData<Boolean> = colorThemeLiveData

    fun switchColorTheme(colorTheme: Boolean) {
        settingsInteractor.switchColorTheme(colorTheme)
        colorThemeLiveData.postValue(
            settingsInteractor.getColorTheme()
        )
    }

    fun openUserAgreement() {
        sharingInteractor.openTerms()
    }

    fun writeToSupport() {
        sharingInteractor.openSupport()
    }

    fun shareApp() {
        sharingInteractor.openSupport()
    }

    override fun onCleared() {
        super.onCleared()
    }

    companion object {
        fun getFactory(
            sharingInteractor: SharingInteractor,
            settingsInteractor: SettingsInteractor
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(
                    sharingInteractor,
                    settingsInteractor
                )
            }
        }
    }
}
