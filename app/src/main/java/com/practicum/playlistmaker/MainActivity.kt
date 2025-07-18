package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnSearch = findViewById<Button>(R.id.btnSearch)
        val btnSearchClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                // Toast.makeText(this@MainActivity, "Нажали на \"Поиск\"!", Toast.LENGTH_SHORT).show()
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
}



