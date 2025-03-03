package com.simon.vpdassesment.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

@HiltAndroidApp
class AndroidApp:Application() {
    override fun onCreate() {
        super.onCreate()
        Napier.base(DebugAntilog())
    }
}