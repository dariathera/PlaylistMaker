package com.practicum.playlistmaker

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import com.practicum.playlistmaker.library.di.libraryViewModelModule
import com.practicum.playlistmaker.player.di.audioplayerModule
import com.practicum.playlistmaker.player.di.audioplayerViewModelModule
import com.practicum.playlistmaker.search.di.searchModule
import com.practicum.playlistmaker.search.di.searchViewModelModule
import com.practicum.playlistmaker.search_history.di.searchHistoryModule
import com.practicum.playlistmaker.settings.di.settingsModule
import com.practicum.playlistmaker.settings.di.settingsViewModelModule
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.sharing.di.sharingModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin

class
App : Application(), KoinComponent {
    val USER_SETTINGS_PREFERENCES = "user_settings_file"
    val DARK_THEME_KEY = "key_for_dark_them"
    val SEARCH_HISTORY_KEY = "key_for_search_history"
    private lateinit var settingsSaver: SettingsInteractor

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                searchModule,
                searchHistoryModule,
                searchViewModelModule,
                audioplayerModule,
                audioplayerViewModelModule,
                settingsModule,
                sharingModule,
                settingsViewModelModule,
                libraryViewModelModule
            )
        }

        setInstance(this)

        settingsSaver = getKoin().get()
        val darkTheme = settingsSaver.getColorTheme()
        settingsSaver.switchColorTheme(darkTheme)
    }

    fun getSystemDarkMode() : Boolean {
        return resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    companion object {
        @Volatile
        private var INSTANCE: App? = null
        fun getInstance(): App? = INSTANCE
        fun setInstance(app: App) {
            INSTANCE = app
        }

        fun getContext(): Context {
            val app = getInstance()
            if (app != null) {
                return app.applicationContext
            } else {
                Log.e(ERROR_LOG_TAG, "Не получилось вернуть applicationContext")
                throw Exception("No applicationContext")
            }
        }

        const val DEBUG_LOG_TAG = "Playlist Maker Debug"
        const val ERROR_LOG_TAG = "Playlist Maker Error"
    }
}