package com.practicum.playlistmaker.library.domain

import android.net.Uri
import android.util.Log
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.library.domain.entities.Playlist
import com.practicum.playlistmaker.library.domain.entities.PlaylistWithNoTracks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class CreatePlaylistUseCaseImpl(
    private val interactor: PlaylistInteractor,
    private val privateStorageSaver: PrivateStorageApi
) : CreatePlaylistUseCase
{
    override suspend fun create(
        name: String,
        description: String,
        uri: Uri?
    ): Unit = withContext(Dispatchers.IO + NonCancellable) { // ← весь блок защищён от отмены
        try {
            val fileName = privateStorageSaver.saveImage(uri)
            val playlistNoTracks = PlaylistWithNoTracks(
                name,
                description,
                fileName
            )
            val playlist = Playlist(
                playlistNoTracks,
                mutableListOf()
            )
            interactor.addNewPlaylist(playlist)
        } catch (e: Exception) {
            Log.e(App.ERROR_LOG_TAG, "Ошибка при создании плейлиста", e)
        }
    }
}