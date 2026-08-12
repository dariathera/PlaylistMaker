package com.practicum.playlistmaker.custom_ui

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaybackButtonViewBinding
import kotlin.properties.Delegates

internal class PlaybackButtonFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    companion object {
        private const val STATE_PLAY = 0
        private const val STATE_PAUSE = 1
    }

    private lateinit var binding: PlaybackButtonViewBinding
    private var state by Delegates.notNull<Int>()
    private lateinit var pauseBitmap: Bitmap
    private lateinit var playBitmap: Bitmap

    init {

        binding = PlaybackButtonViewBinding.inflate(
            LayoutInflater.from(context),
            this
        )

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                state = getInt(R.styleable.PlaybackButtonView_state, STATE_PLAY)
                when (state) {
                    STATE_PLAY -> binding.button.setImageResource(R.drawable.ic_play_512)
                    STATE_PAUSE -> binding.button.setImageResource(R.drawable.ic_pause_512)
                }
                pauseBitmap = getDrawable(R.styleable.PlaybackButtonView_pauseImageResId)?.toBitmap() ?:
                    ContextCompat.getDrawable(context, R.drawable.ic_custom_pause_84)!!.toBitmap()
                playBitmap = getDrawable(R.styleable.PlaybackButtonView_playImageResId)?.toBitmap() ?:
                    ContextCompat.getDrawable(context, R.drawable.ic_custom_play_84)!!.toBitmap()
                isClickable = true
                Log.d("сlick", "блок try завершён")
            } finally {
                recycle()
            }
        }
        Log.d("сlick",
                "View.getWidth() = ${width} \n " +
                "getHeight() = ${height}")

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.d("сlick", "isClickable = $isClickable, isEnabled = $isEnabled")
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true // продолжаем обрабатывать касание
            }
            MotionEvent.ACTION_UP -> {

                Log.d("сlick", "нажатие onTouchEvent")
                performClick() // вызываем системный клик
                return true
            }
        }
        return super.onTouchEvent(event)
    }



    override fun performClick(): Boolean {
        Log.d("сlick", "нажатие performClick")
        // Обрабатываем нажатие
        toggleState()

        // Вызываем супер для поддержки слушателей, но игнорируем его результат
        super.performClick()

        // Сообщаем, что событие обработано, не зависимо от того, есть ли слушатели во Fragment
        return true
    }

    private fun toggleState() {
        try {
            when (state) {
                STATE_PLAY -> {
                    state = STATE_PAUSE
                    binding.button.setImageResource(R.drawable.ic_pause_512)
                }
                STATE_PAUSE -> {
                    state = STATE_PLAY
                    binding.button.setImageResource(R.drawable.ic_play_512)
                }
            }
        } catch  (e: Exception) {
            Log.e(App.ERROR_LOG_TAG, "Ошибка в PlaybackButtonView.toggleState", e)
        }
    }

}