package com.practicum.playlistmaker.library.domain

import android.net.Uri
import android.util.Log
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.library.domain.entities.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class UpdatePlaylistUseCaseImpl(
    private val interactor: PlaylistInteractor,
    private val privateStorageSaver: PrivateStorageApi
) : UpdatePlaylistUseCase
{
    override suspend fun update(
        playlistId: Long,
        name: String,
        description: String,
        uri: Uri?
    ): Unit = withContext(Dispatchers.IO + NonCancellable) {
        try {
            val playlist: Playlist? = interactor.getPlaylistById(playlistId)
            playlist?.playlist?.name = name
            playlist?.playlist?.description = description
            val oldCoverFileName = playlist?.playlist?.coverFileName
            playlist?.playlist?.coverFileName = privateStorageSaver.saveImage(uri)
            interactor.updatePlaylist(playlist)
            if (oldCoverFileName != playlist?.playlist?.coverFileName) {
                privateStorageSaver.deleteImage(oldCoverFileName)
            }
        } catch (e: Exception) {
            Log.e(App.ERROR_LOG_TAG, "Ошибка при обновлении плейлиста", e)
        }
    }
}