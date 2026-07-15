package com.practicum.playlistmaker.library.data

import android.app.Activity
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
                        Log.d("ImageSave", "PrivateStorage: сгенерировано имя $fileName")

                        val file = File(filePath, fileName)

                        context.contentResolver.openInputStream(uri)?.use { inputStream ->
                            FileOutputStream(file).use { outputStream ->
                                val bitmap = BitmapFactory.decodeStream(inputStream)
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                                bitmap.recycle()
                            }
                        } ?: return@withContext null
                        Log.d("ImageSave", "PrivateStorage: перед возвратом fileName = $fileName")
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
/*
    override suspend fun saveImage(uri: Uri?): String? =
        withContext(Dispatchers.IO) {
            if (uri == null) return@withContext null

            try {
                // 1. Получаем размеры изображения
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream, null, options)
                }

                // 2. Рассчитываем коэффициент уменьшения (sample size)
                // Желаемый размер для обложки, например, 512x512px
                val TARGET_SIZE = 512
                options.inSampleSize = calculateInSampleSize(options, TARGET_SIZE, TARGET_SIZE)
                options.inJustDecodeBounds = false

                // 3. Декодируем изображение с нужным размером
                val bitmap = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream, null, options)
                } ?: return@withContext null

                // 4. Сохраняем bitmap
                val filePath = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "playlist_maker_album"
                )
                if (!filePath.exists()) filePath.mkdirs()

                val fileName = "cover_${System.currentTimeMillis()}.jpg"
                val file = File(filePath, fileName)

                FileOutputStream(file).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                }
                bitmap.recycle()

                return@withContext fileName
            } catch (e: Throwable) {
                return@withContext null
            }
        }

    // Вспомогательная функция для расчета inSampleSize
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.outHeight to options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
    */
}