package com.practicum.playlistmaker.root.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.util.SingleLiveEvent

class SharedViewModel : ViewModel() {
    private val showMessageLiveData = MutableLiveData<String>("")

    fun observeShowMessage(): LiveData<String> = showMessageLiveData

    fun setToastMessage(message: String?) {
        if (!message.isNullOrEmpty()) {
            Log.d("NewPlaylist", "Установка сообщения для тоста")
            showMessageLiveData.postValue(message!!)
        } else {
            showMessageLiveData.postValue("")
        }
    }
}