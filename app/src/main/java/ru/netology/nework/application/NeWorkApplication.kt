package ru.netology.nework.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ru.netology.nework.authentication.AppAuth

@HiltAndroidApp
class NeWorkApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppAuth.initApp(this)
    }
}