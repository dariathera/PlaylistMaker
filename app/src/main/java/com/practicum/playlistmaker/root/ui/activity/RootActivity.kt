package com.practicum.playlistmaker.root.ui.activity

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityRootBinding
import com.practicum.playlistmaker.library.ui.viewmodel.PlaylistCreatorViewModel
import com.practicum.playlistmaker.root.ui.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.scope.activityScope
import org.koin.androidx.viewmodel.ext.android.viewModel

class RootActivity : AppCompatActivity() {

    companion object {
        private const val NOTIFICATION_DISPLAY_DURATION = 2000L
    }

    private lateinit var binding: ActivityRootBinding
    private var keyboardLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    private val sharedViewModel: SharedViewModel by viewModel()
    private var navigationBarHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Получаем высоту навигационной панели через WindowInsets (рекомендуемый способ)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            insets
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.playlistCreatorFragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                    setupKeyboardResize(true)
                }
                R.id.audioplayerFragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                    setupKeyboardResize(false)
                }
                else -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                    setupKeyboardResize(false)
                }
            }
        }

        sharedViewModel.observeShowMessage().observe(this) { message ->
            if (message.isNotEmpty()) {
                lifecycleScope.launch {
                    Log.d("NewPlaylist", "Сейчас мы длжны показать уведомление о создании плейлиста")
                    // Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    binding.notificationTextView.text = message
                    val params = binding.notificationTextView.layoutParams as? ConstraintLayout.LayoutParams
                    params?.bottomMargin = navigationBarHeight
                    binding.notificationTextView.layoutParams = params
                    sharedViewModel.setToastMessage(null)
                    binding.notificationTextView.visibility = View.VISIBLE
                    delay(NOTIFICATION_DISPLAY_DURATION)
                    binding.notificationTextView.visibility = View.GONE
                }
            }
        }
    }

    /**
     * Включает или выключает автоматическую корректировку отступов при появлении клавиатуры.
     * @param isEnabled true – слушатель добавлен, false – удалён.
     */
    private fun setupKeyboardResize(isEnabled: Boolean) {
        val rootView = binding.main

        // Если слушатель уже был, удаляем его
        keyboardLayoutListener?.let {
            rootView.viewTreeObserver.removeOnGlobalLayoutListener(it)
            keyboardLayoutListener = null
        }

        if (!isEnabled) {
            rootView.setPadding(0, 0, 0, 0)
            return
        }

        // Создаём новый слушатель
        val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
            private var previousKeypadHeight = 0

            override fun onGlobalLayout() {
                val rect = Rect()
                rootView.getWindowVisibleDisplayFrame(rect)
                val screenHeight = rootView.height
                val keypadHeight = screenHeight - rect.bottom

                if (keypadHeight != previousKeypadHeight) {
                    previousKeypadHeight = keypadHeight
                    val isKeyboardVisible = keypadHeight > screenHeight * 0.15
                    val bottomPadding = if (isKeyboardVisible) keypadHeight else 0
                    rootView.setPadding(0, 0, 0, bottomPadding)
                    if (isKeyboardVisible) {
                        Log.d("Keyboard", "клавиатура открыта, отступ = $keypadHeight")
                    }
                }
            }
        }

        rootView.viewTreeObserver.addOnGlobalLayoutListener(listener)
        keyboardLayoutListener = listener
    }

    override fun onDestroy() {
        super.onDestroy()
        // На всякий случай удаляем слушатель, если активность уничтожена
        keyboardLayoutListener?.let {
            binding.main.viewTreeObserver.removeOnGlobalLayoutListener(it)
            keyboardLayoutListener = null
        }
    }

    fun animateBottomNavigationView() {
        binding.bottomNavigationView.visibility = View.GONE
    }
}