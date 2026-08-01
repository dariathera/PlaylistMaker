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
            val oldPlaylist: Playlist? = interactor.getPlaylistById(playlistId)
            if (oldPlaylist == null) {
                throw IllegalArgumentException("Внутри UpdatePlaylistUseCaseImpl получен playlist == null")
            }
            val newPlaylistNoTracks = oldPlaylist.playlist.copy(
                name = name,
                description = description
            )
            val newPlaylist = oldPlaylist.copy(
                playlist = newPlaylistNoTracks
            )
            newPlaylist.playlist.coverFileName = privateStorageSaver.saveImage(uri)
            interactor.updatePlaylist(newPlaylist)
            if (oldPlaylist.playlist.coverFileName != newPlaylist.playlist.coverFileName) {
                privateStorageSaver.deleteImage(oldPlaylist.playlist.coverFileName)
            }
        } catch (e: IllegalArgumentException) {
            Log.e(App.ERROR_LOG_TAG, e.message, e)
        } catch (e: Exception) {
            Log.e(App.ERROR_LOG_TAG, "Ошибка при обновлении плейлиста", e)
        }
    }
}
