package com.practicum.playlistmaker.library.domain

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.practicum.playlistmaker.library.domain.entities.Playlist
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
            Log.d("ImageSave", "CreatePlaylistUseCaseImpl работает")
            val fileName = privateStorageSaver.saveImage(uri)
            Log.d("ImageSave", "CreatePlaylistUseCaseImpl получил fileName = $fileName")
            val playlist = Playlist(name, description, fileName)
            Log.d("ImageSave", "CreatePlaylistUseCaseImpl создал playlist: $playlist")
            interactor.addNewPlaylist(playlist)
            Log.d("ImageSave", "CreatePlaylistUseCaseImpl плейлист сохранён в БД")
        } catch (e: Exception) {
            Log.e("ImageSave", "Ошибка при создании плейлиста", e)
        }
    }

}
    /* override suspend fun create(
        name: String,
        description: String,
        uri: Uri?
    ) {

        Log.d("DataBas", "Запускаем CreatePlaylistUseCaseImpl")
        val playlist: Playlist = Playlist(name, description)
        interactor.addNewPlaylist(playlist)
        val fileName: String = playlist.id.toString()
        Log.d("DataBas", "playlist.id: ${playlist.id}")
        Log.d("DataBas", "fileName: $fileName")
        // fileName = "123"
        val privateStorageUri: Uri? = privateStorageSaver.saveImage(uri, fileName)
        playlist.uri = privateStorageUri
        interactor.updatePlaylist(playlist)
        Log.d("DataBas", "playlist.id: ${playlist.id}")
    }

     */
