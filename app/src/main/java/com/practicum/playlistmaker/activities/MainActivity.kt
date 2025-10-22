package com.practicum.playlistmaker.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.objects.Track


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Если в прошлый раз работа приложения завершилась на экране аудиоплеера, перенаправляем пользователся на экран аудиоплеера
        val app = App.getInstance()
        if (app != null) {
            val sharedPrefs : SharedPreferences = app.getSharedPreferences(
                app.USER_SETTINGS_PREFERENCES, MODE_PRIVATE)
            val lastScreen = sharedPrefs.getString(app.LAST_SCREEN_KEY, "main")  // "main" — дефолт
            if (lastScreen == "audioplayer") {
                val intent = Intent(this, AudioplayerActivity::class.java)
                startActivity(intent)

                // Не выходим из main activity, чтобы на экране аудиоплеера корректно работала кнопка назад
                // finish()
                // return
            }
        }

        // Если пользователь остаётся на главном экране

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnSearch = findViewById<Button>(R.id.btnSearch)
        val btnSearchClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val displayIntent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(displayIntent)
            }
        }
        btnSearch.setOnClickListener(btnSearchClickListener)

        val btnSettings = findViewById<Button>(R.id.btnSettings)
        btnSettings.setOnClickListener {
            // Toast.makeText(this@MainActivity, "Нажали на \"Настройки\"!", Toast.LENGTH_LONG).show()
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
        }

        val btnLibrary = findViewById<Button>(R.id.btnLibrary)
        btnLibrary.setOnClickListener {
            // Toast.makeText(this@MainActivity, "Нажали на \"Медиатека\"!", Toast.LENGTH_SHORT).show()
            val displayIntent = Intent(this, LibraryActivity::class.java)
            startActivity(displayIntent)
        }
    }

    override fun onPause() {
        super.onPause()
        val app = App.getInstance()
        if (app != null) {
            val sharedPrefs: SharedPreferences = app.getSharedPreferences(
                app.USER_SETTINGS_PREFERENCES, MODE_PRIVATE
            )
            sharedPrefs.edit().putString(app.LAST_SCREEN_KEY, "main").apply()
        }
    }
}




