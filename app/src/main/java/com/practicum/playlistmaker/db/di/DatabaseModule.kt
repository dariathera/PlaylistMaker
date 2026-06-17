package com.practicum.playlistmaker.db.di

import androidx.room.Room
import com.practicum.playlistmaker.db.data.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }

}