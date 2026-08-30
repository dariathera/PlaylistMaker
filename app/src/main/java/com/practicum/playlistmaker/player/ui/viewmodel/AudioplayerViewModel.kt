package com.practicum.playlistmaker.player.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.domain.FavoritesInteractor
import com.practicum.playlistmaker.library.domain.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.PrivateStorageApi
import com.practicum.playlistmaker.library.domain.entities.Playlist
import com.practicum.playlistmaker.library.domain.entities.PlaylistGeneralInformation
import com.practicum.playlistmaker.root.ui.viewmodel.SharedViewModel
import com.practicum.playlistmaker.search.domain.entities.Track
import com.practicum.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AudioplayerViewModel(
    private val track: Track,
    private val favoritesInteractor : FavoritesInteractor,
    private val playlistInteractor: PlaylistInteractor,
    private val privateStorageApi: PrivateStorageApi,
    private val context: Context
) : ViewModel() {

    private val showMessageLiveData = SingleLiveEvent<String>()
    fun observeShowMessage(): LiveData<String> = showMessageLiveData

    private val isFavoriteLiveData = MutableLiveData<Boolean>(false)
    fun observeIsFavorite(): LiveData<Boolean> = isFavoriteLiveData

    private val playlistsLiveData = MutableLiveData<List<PlaylistGeneralInformation>>(
        listOf<PlaylistGeneralInformation>()
    )
    fun observePlaylists(): LiveData<List<PlaylistGeneralInformation>> = playlistsLiveData

    private val hideBottomSheetLiveData = SingleLiveEvent<Unit>()
    fun observeHideBottomSheet(): LiveData<Unit> = hideBottomSheetLiveData

    init {
        postValueisFavoriteLiveData()
    }

    fun onFavoriteClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            val trackIsFavorite = isFavoriteLiveData.value ?: false
            if (trackIsFavorite) {
                favoritesInteractor.deleteFavorite(track)
            } else {
                favoritesInteractor.addNewFavorite(track)
            }
            postValueisFavoriteLiveData()
        }
    }

    private fun postValueisFavoriteLiveData() {
        viewModelScope.launch(Dispatchers.IO) {
            val favoriteTracks: MutableList<Track> = mutableListOf<Track>()
            favoriteTracks.addAll(favoritesInteractor.getAllFavorites().firstOrNull() ?: emptyList())
            isFavoriteLiveData.postValue(track in favoriteTracks)
        }
    }

    fun requestPlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            val playlists : List<PlaylistGeneralInformation> = playlistInteractor.getAllPlaylistsGeneralInfo()
            for (playlist in playlists) {
                playlist.uri = privateStorageApi.getFileUri(playlist.coverFileName)
            }
            playlistsLiveData.postValue(playlists)
        }
    }

    fun handleClickOnPlaylist(id: Long, sharedViewModel: SharedViewModel){
        viewModelScope.launch(Dispatchers.IO) {
            val trackIsAlreadyIncluded = async { isTrackIncludedById(id) }
            if (trackIsAlreadyIncluded.await()) {
                notifyTrackAlreadyAdded(id, sharedViewModel)
                } else {
                addTrackToPlaylist(id, sharedViewModel)
                hideBottomSheetLiveData.postValue(Unit)
            }
        }
    }

    private suspend fun notifyTrackAlreadyAdded(id: Long, sharedViewModel: SharedViewModel) {
        val playlistName : String? = playlistInteractor.getPlaylistNameByPlaylistId(id)
        if (!playlistName.isNullOrEmpty()) {
            sharedViewModel.setToastMessage(
                context.getString(R.string.track_already_added, playlistName)
            )
        }
    }

    private suspend fun isTrackIncludedById(id: Long) : Boolean {
        val trackIdList: List<Long> = playlistInteractor.getTracksIdListByPlaylistId(id)
        var trackIsAlreadyIncluded = false
        for (element in trackIdList) {
            if (element == track.trackId) {
                trackIsAlreadyIncluded = true
                break
            }
        }
        return trackIsAlreadyIncluded
    }

    // Есть риск добавить трек повторно.
    // Я не проверяю, включён ли трек в плейлист -
    // это должно было быть на предыдущем этапе.
    private suspend fun addTrackToPlaylist(id: Long, sharedViewModel: SharedViewModel){
        val playlist: Playlist? = playlistInteractor.getPlaylistById(id)
        if (playlist != null) {
            playlist.trackList.add(track)
            playlistInteractor.updatePlaylist(playlist)
            sharedViewModel.setToastMessage(
                context.getString(R.string.added_to_playlist, playlist.playlist.name)
            )
        }
    }

    companion object {
        private const val START_TIME_TEXT = "00:00"
    }

}
