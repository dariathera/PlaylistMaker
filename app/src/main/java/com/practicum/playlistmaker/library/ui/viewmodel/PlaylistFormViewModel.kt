package com.practicum.playlistmaker.library.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.domain.CreatePlaylistUseCase
import com.practicum.playlistmaker.library.domain.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.PrivateStorageApi
import com.practicum.playlistmaker.library.domain.UpdatePlaylistUseCase
import com.practicum.playlistmaker.library.domain.entities.Playlist
import com.practicum.playlistmaker.library.ui.activity.EditPlaylistLoadedData
import com.practicum.playlistmaker.root.ui.viewmodel.SharedViewModel
import com.practicum.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistFormViewModel(
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val playlistId: Long,
    private val playlistInteractor: PlaylistInteractor,
    private val privateStorageApi: PrivateStorageApi,
    private val updatePlaylistUseCase: UpdatePlaylistUseCase
) : ViewModel() {

    companion object {
        private const val KEY_NAME = "playlist_name"
        private const val KEY_DESCRIPTION = "playlist_description"
        private const val KEY_URI = "playlist_uri"
    }

    var initialName = ""
    var initialDescription = ""
    var initialUri: Uri? = null

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

    private val loadedDataLiveData = SingleLiveEvent<EditPlaylistLoadedData>()
    fun observeLoadedData(): LiveData<EditPlaylistLoadedData> = loadedDataLiveData

    init {
        if (playlistId >= 0) {
            loadPlaylistData()
        }
    }

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

    fun formIsChanged() : Boolean {
        return !( name == initialName &&
                  description == initialDescription &&
                  uri == initialUri )
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

    private fun loadPlaylistData() {
        viewModelScope.launch(Dispatchers.IO) {
            val playlist: Playlist? = playlistInteractor.getPlaylistById(playlistId)
            if (playlist == null) {
                Log.e(App.Companion.ERROR_LOG_TAG, "В конструктор PlaylistFormViewModel передан id несуществующего плейлиста")
            } else {
                initialName = playlist.playlist.name
                name = playlist.playlist.name
                initialDescription = playlist.playlist.description
                description = playlist.playlist.description
                initialUri = privateStorageApi.getFileUri(playlist.playlist.coverFileName)
                uri = privateStorageApi.getFileUri(playlist.playlist.coverFileName)
                loadedDataLiveData.postValue(
                    EditPlaylistLoadedData(
                        playlist.playlist.name,
                        playlist.playlist.description,
                        privateStorageApi.getFileUri(playlist.playlist.coverFileName)
                    )
                )
                isSaveButtonEnabledLiveData.postValue(
                    playlist.playlist.name.isNotEmpty()
                )
            }
        }
    }

    fun updatePlaylist(sharedViewModel: SharedViewModel) {
        if (!name.isEmpty() && formIsChanged()) {
            viewModelScope.launch(Dispatchers.IO) {
                updatePlaylistUseCase.update(
                    playlistId,
                    name,
                    description,
                    uri
                )
                isSavingCompletedLiveData.postValue(true)
                sharedViewModel.setToastMessage(
                    context.getString(R.string.changes_saved)
                )
            }
        }
    }
}