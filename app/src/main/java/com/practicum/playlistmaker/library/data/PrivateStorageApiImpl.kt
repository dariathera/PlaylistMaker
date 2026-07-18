package com.practicum.playlistmaker.library.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import com.practicum.playlistmaker.library.domain.PrivateStorageApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.cancellation.CancellationException

class PrivateStorageApiImpl(private val context: Context) : PrivateStorageApi {

    override suspend fun saveImage(uri: Uri?): String? =
        withContext(Dispatchers.IO) {
            try {
                withContext(NonCancellable) {
                    if (uri == null) return@withContext null

                    try {
                        val filePath = File(
                            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                            "playlist_maker_album"
                        )
                        if (!filePath.exists()) filePath.mkdirs()

                        // Генерируем уникальное имя: время в миллисекундах + случайное число
                        val timestamp = System.currentTimeMillis()
                        val random = (1000..9999).random()
                        val fileName = "cover_${timestamp}_$random.jpg"
                        val file = File(filePath, fileName)
                        context.contentResolver.openInputStream(uri)?.use { inputStream ->
                            FileOutputStream(file).use { outputStream ->
                                val bitmap = BitmapFactory.decodeStream(inputStream)
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                                bitmap.recycle()
                            }
                        } ?: return@withContext null
                        return@withContext fileName
                    } catch (e: Throwable) {
                        Log.e("ImageSave", "PrivateStorage: Ошибка сохранения", e)
                        null
                    }
                }
            } catch (e: CancellationException) {
                Log.e("ImageSave", "PrivateStorage: Ошибка сохранения", e)
                null
            }
        }

    override fun getFileUri(fileName: String?): Uri? {
        if (fileName.isNullOrBlank()) return null

        val directory = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "playlist_maker_album"
        )
        val file = File(directory, fileName)

        return if (file.exists()) file.toUri() else null
    }
}