package com.mhmdjalal.newsapp

import android.app.Application
import com.mhmdjalal.newsapp.di.appModules
import com.mhmdjalal.newsapp.di.networkModules
import com.mhmdjalal.newsapp.di.repositoryModules
import com.mhmdjalal.newsapp.di.viewModelModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {


    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(listOf(
                appModules,
                viewModelModules,
                networkModules,
                repositoryModules
            ))
        }
    }
}