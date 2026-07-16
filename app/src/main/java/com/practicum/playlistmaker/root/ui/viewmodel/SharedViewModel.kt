package com.practicum.playlistmaker.root.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val showMessageLiveData = MutableLiveData<String>("")

    fun observeShowMessage(): LiveData<String> = showMessageLiveData

    fun setToastMessage(message: String?) {
        if (!message.isNullOrEmpty()) {
            showMessageLiveData.postValue(message!!)
        } else {
            showMessageLiveData.postValue("")
        }
    }
}