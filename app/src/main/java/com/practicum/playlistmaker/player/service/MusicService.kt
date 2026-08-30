package com.practicum.playlistmaker.player.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.ui.mediaplayer.MediaplayerState
import com.practicum.playlistmaker.player.ui.timer.TimeTextObserving
import com.practicum.playlistmaker.player.ui.timer.TimerManager
import com.practicum.playlistmaker.root.ui.activity.RootActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

internal class MusicService() : Service(), TimeTextObserving, MusicServiceApi {

    inner class MusicServiceBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    private val binder = MusicServiceBinder()

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    companion object {
        private const val LOG_TAG = "MusicService"
        const val TRACK_KEY = "song_url"
        const val ARTIST_NAME_KEY = "artist_name"
        const val TRACK_NAME_KEY = "track_name"
        private const val START_TIME_TEXT = "00:00"

    }

    private val mediaPlayer : MediaPlayer by inject()
    private lateinit var timerManager: TimerManager
    private val CHANNEL_ID = "music_channel"
    private val NOTIFICATION_ID = 1
    private var songUrl = ""
    private var artistName = ""
    private var trackName = ""
    private var playerState : MediaplayerState = MediaplayerState.DEFAULT
    private var savedPlayerPosition: Int = 0
    private var savedIsPlaying: Boolean = true

    private val _isPlaybackCompleted = MutableStateFlow<Boolean>(false)
    override val isPlaybackCompleted = _isPlaybackCompleted.asStateFlow()
    private val _isPlaying = MutableStateFlow<Boolean>(savedIsPlaying)
    override val isPlaying = _isPlaying.asStateFlow()
    private val _timeText = MutableStateFlow<String>(START_TIME_TEXT)
    override val timeText = _timeText.asStateFlow()

    // Инициализация ресурсов
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        timerManager = getKoin().get { parametersOf(mediaPlayer) }
        timerManager.addListener(this)
    }

    // Освобождение ресурсов
    override fun onDestroy() {
        releasePlayer()
        // сброс состояния экрана на начало воспроизведения
        playerState = MediaplayerState.PREPARED
        _isPlaying.value = false
        _timeText.value = START_TIME_TEXT
        timerManager.clearTasks()
        _isPlaybackCompleted.value = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        songUrl = intent?.getStringExtra(TRACK_KEY) ?: ""
        trackName = intent?.getStringExtra(TRACK_NAME_KEY) ?: ""
        artistName = intent?.getStringExtra(ARTIST_NAME_KEY) ?: ""
        if (playerState == MediaplayerState.DEFAULT) {
            initMediaPlayer()
        }
        return START_NOT_STICKY
    }

    // Освобождаем все ресурсы, выделенные для плеера
    private fun releasePlayer() {
        mediaPlayer.stop()
        mediaPlayer.setOnPreparedListener(null)
        mediaPlayer.setOnCompletionListener(null)
        mediaPlayer.release()
    }

    private fun initMediaPlayer() {
        if (songUrl.isEmpty()) {
            Log.e(LOG_TAG, "URL пустой или отсутствует")
            stopSelf()
            return
        }
        try {
            mediaPlayer.apply {
                reset() // сбрасываем предыдущее состояние
                setDataSource(songUrl)

                // Устанавливаем слушатели ДО prepareAsync

                setOnPreparedListener {
                    if (savedPlayerPosition > 0) {
                        mediaPlayer.seekTo(savedPlayerPosition)
                    } else {
                        _timeText.value = START_TIME_TEXT
                    }
                    playerState = if (savedIsPlaying) {
                        mediaPlayer.start()
                        timerManager.startTimer()
                        MediaplayerState.PLAYING
                    } else {
                        MediaplayerState.PREPARED
                    }
                    _isPlaying.value = savedIsPlaying
                }

                setOnCompletionListener {
                    stopSelf()
                }

                setOnErrorListener { _, what, extra ->
                    Log.e(LOG_TAG, "Ошибка MediaPlayer: what=$what, extra=$extra")
                    stopSelf()
                    true
                }

                prepareAsync() // запускаем подготовку после настройки слушателей
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Ошибка при установке источника", e)
            stopSelf()
        }
    }

    // Запуск воспроизведения
    private fun startPlayer() {
        mediaPlayer.start()
        playerState = MediaplayerState.PLAYING
        _isPlaying.value = true

    }

    // Приостановка воспроизведения
    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = MediaplayerState.PAUSED
        _isPlaying.value = false
        // Сохраняем позицию при паузе
        savedPlayerPosition = mediaPlayer.currentPosition
    }

    override fun setNewTimeText(timeText: String) {
        _timeText.value = timeText
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Музыкальный плеер",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Уведомления для фонового воспроизведения"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)

        }
    }

    private fun createNotification(): Notification {
        // Создаём PendingIntent (пример)
        val intent = Intent(this, RootActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(
                "%s - %s".format(artistName, trackName)
            )
            .setSmallIcon(R.drawable.ic_notification) // иконка
            .setContentIntent(pendingIntent)
            .setOngoing(true) // нельзя смахнуть

        return builder.build()
    }

    private fun getForegroundServiceTypeConstant(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
        }
    }

    override fun playbackControl() {
        when(playerState) {
            MediaplayerState.PLAYING -> {
                pausePlayer()
                timerManager.stopTimer()
            }
            MediaplayerState.PREPARED, MediaplayerState.PAUSED -> {
                startPlayer()
                timerManager.startTimer()
            }
            MediaplayerState.DEFAULT -> {
                Log.e(
                    App.Companion.ERROR_LOG_TAG, "Недопустимая ситуация: реализуется " +
                            "ветка DEFAULT в функции playbackControl(). Это значит, что ранее по " +
                            "какой-то причине функция preparePlayer() не была вызвана.")
            }
        }
    }

    override fun showNotifications() {
        if (playerState == MediaplayerState.PLAYING) {
            ServiceCompat.startForeground(
                /* service = */ this,
                /* id = */ NOTIFICATION_ID,
                /* notification = */ createNotification(),
                /* foregroundServiceType = */ getForegroundServiceTypeConstant()
            )
        }
    }

    override fun hideNotifications() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

}