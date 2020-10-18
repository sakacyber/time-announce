package com.saka.android.timeannounce

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Prefs.init(this)
    }
}