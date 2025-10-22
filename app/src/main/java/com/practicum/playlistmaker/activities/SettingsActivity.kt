package com.practicum.playlistmaker.activities

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnGoBack = findViewById<Button>(R.id.btnGoBack)
        btnGoBack.setOnClickListener {
            finish() //Удаляет SettingsActivity из стека.
        }

        val btnShareTheApp = findViewById<ImageView>(R.id.share_the_app)
        btnShareTheApp.setOnClickListener {
            val message = getString(R.string.android_developer_course_link)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_to)))
        }

        val btnWriteToSupport = findViewById<ImageView>(R.id.write_to_support)
        btnWriteToSupport.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.developers_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_of_mail_to_developers))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.text_of_mail_to_developers))
            }
            startActivity(supportIntent)
        }

        val btnUserAgreement = findViewById<ImageView>(R.id.user_agreement)
        btnUserAgreement.setOnClickListener {
            val url = Uri.parse(getString(R.string.user_agreement_link))
            val userAgreementIntent = Intent(Intent.ACTION_VIEW, url)
            startActivity(userAgreementIntent)
        }

        val themeSwitcher = findViewById<
                com.google.android.material.switchmaterial.SwitchMaterial>(R.id.themeSwitcher)
        themeSwitcher.setChecked((applicationContext as App).darkTheme)
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            switcher.setChecked((applicationContext as App).darkTheme)
        }
    }

    override fun onPause() {
        super.onPause()
        val app = App.getInstance()
        if (app != null) {
            val sharedPrefs: SharedPreferences = app.getSharedPreferences(
                app.USER_SETTINGS_PREFERENCES, MODE_PRIVATE
            )
            sharedPrefs.edit().putString(app.LAST_SCREEN_KEY, ScreenState.SETTINGS.displayName).apply()
        }
    }
}
