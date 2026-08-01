package com.practicum.playlistmaker.library.domain

import android.net.Uri

interface PrivateStorageApi {
    suspend fun saveImage(uri: Uri?): String?
    fun getFileUri(fileName: String?): Uri?
    suspend fun deleteImage(fileName: String?): Boolean
}