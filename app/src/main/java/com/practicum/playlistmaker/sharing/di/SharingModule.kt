package com.practicum.playlistmaker.sharing.di

import android.content.Context
import com.practicum.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.SharingInteractorImpl
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val sharingModule = module {

    single<ExternalNavigator> { (context: Context) ->
        ExternalNavigatorImpl(context)
    }

    single<SharingInteractor> { (context: Context) ->
        SharingInteractorImpl(
            context,
            get { parametersOf(context) }
        )
    }
}