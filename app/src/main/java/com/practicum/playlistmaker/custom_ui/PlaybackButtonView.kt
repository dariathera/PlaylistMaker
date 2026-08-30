package com.practicum.playlistmaker.custom_ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import kotlin.properties.Delegates

internal class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    companion object {
        private const val STATE_PLAY = 0
        private const val STATE_PAUSE = 1
    }

    private var state by Delegates.notNull<Int>()
    private lateinit var pauseBitmap: Bitmap
    private lateinit var playBitmap: Bitmap
    private var imageRect = RectF(0f, 0f, 0f, 0f)

    init {

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                state = getInt(R.styleable.PlaybackButtonView_state, STATE_PLAY)
                pauseBitmap = getDrawable(R.styleable.PlaybackButtonView_pauseImageResId)?.toBitmap() ?:
                    ContextCompat.getDrawable(context, R.drawable.ic_custom_pause_84)!!.toBitmap()
                playBitmap = getDrawable(R.styleable.PlaybackButtonView_playImageResId)?.toBitmap() ?:
                    ContextCompat.getDrawable(context, R.drawable.ic_custom_play_84)!!.toBitmap()
                isClickable = true
            } finally {
                recycle()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        when (state) {
            STATE_PLAY -> {
                playBitmap.let {
                    canvas.drawBitmap(playBitmap, null, imageRect, null)
                }
            }
            STATE_PAUSE -> {
                pauseBitmap.let {
                    canvas.drawBitmap(pauseBitmap, null, imageRect, null)
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true // продолжаем обрабатывать касание
            }
            MotionEvent.ACTION_UP -> {
                performClick() // вызываем системный клик
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
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
                    invalidate()
                }
                STATE_PAUSE -> {
                    state = STATE_PLAY
                    invalidate()
                }
            }
        } catch  (e: Exception) {
            Log.e(App.ERROR_LOG_TAG, "Ошибка в PlaybackButtonView.toggleState", e)
        }
    }

    fun setStatePlay() {
        if (state != STATE_PLAY) {
            state = STATE_PLAY
            invalidate()
        }
    }

    fun setStatePause() {
        if (state != STATE_PAUSE) {
            state = STATE_PAUSE
            invalidate()
        }
    }

}