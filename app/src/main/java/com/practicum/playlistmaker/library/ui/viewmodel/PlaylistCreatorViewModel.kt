package com.practicum.playlistmaker.library.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.domain.CreatePlaylistUseCase
import com.practicum.playlistmaker.root.ui.viewmodel.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistCreatorViewModel(
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val context: Context,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_NAME = "playlist_name"
        private const val KEY_DESCRIPTION = "playlist_description"
        private const val KEY_URI = "playlist_uri"
    }

    // Геттеры и сеттеры через savedStateHandle
    var name: String
        get() = savedStateHandle[KEY_NAME] ?: ""
        set(value) = savedStateHandle.set(KEY_NAME, value)

    var description: String
        get() = savedStateHandle[KEY_DESCRIPTION] ?: ""
        set(value) = savedStateHandle.set(KEY_DESCRIPTION, value)

    var uri: Uri?
        get() = savedStateHandle.get<String>(KEY_URI)?.let { Uri.parse(it) }
        set(value) = savedStateHandle.set(KEY_URI, value?.toString())


    private val isSaveButtonEnabledLiveData = MutableLiveData<Boolean>(false)
    fun observeIsSaveButtonEnabled(): LiveData<Boolean> = isSaveButtonEnabledLiveData

    private val isSavingCompletedLiveData = MutableLiveData<Boolean>(false)
    fun observeIsSavingCompleted(): LiveData<Boolean> = isSavingCompletedLiveData



    fun  handleNameInput(s: CharSequence?) {
        name = s?.toString() ?: ""
        isSaveButtonEnabledLiveData.postValue(!name.isEmpty())
    }

    fun handleDescriptionInput(s: CharSequence?) {
        description = s?.toString() ?: ""
    }

    fun handleImageURI(_uri: Uri?) {
        uri = _uri
    }

    fun formIsEmpty() : Boolean {
        return name.isEmpty() && description.isEmpty() && uri == null
    }

    fun createNewPlaylist(sharedViewModel: SharedViewModel) {
        if (!name.isEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                createPlaylistUseCase.create(
                    name,
                    description,
                    uri
                )
                isSavingCompletedLiveData.postValue(true)
                sharedViewModel.setToastMessage(
                    context.getString(R.string.playlist_created_message, name)
                )
            }
        }
    }
}