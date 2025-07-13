package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnSettings = findViewById<Button>(R.id.btnGoBack)
        btnSettings.setOnClickListener {
            val displayIntent = Intent(this, MainActivity::class.java)
            // FLAG_ACTIVITY_CLEAR_TOP очищает все активности выше MainActivity в стеке, если она уже существует.
            // FLAG_ACTIVITY_NEW_TASK создает новую задачу для MainActivity, если она еще не существует.
            displayIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(displayIntent)
            finish() //Удаляет SettingsActivity из стека.
        }
    }
}
