package com.practicum.playlistmaker.library.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityLibraryBinding

class LibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLibraryBinding

    private lateinit var tabMediator: TabLayoutMediator

    private val space = "    "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnGoBack.setOnClickListener {
            finish()
        }

        binding.viewPager.adapter = LibraryViewPagerAdapter(supportFragmentManager, lifecycle)

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when(position) {
                0 -> tab.text = space + getString(R.string.tab_name_favorites) + space
                1 -> tab.text = space + getString(R.string.tab_name_playlists) + space
            }
        }
        tabMediator.attach()

    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }

}