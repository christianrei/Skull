package com.dimmaranch.skull

import android.app.Application
import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp

class SkullApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        MobileAds.initialize(this) {}
        appContext = applicationContext
    }

    companion object {
        lateinit var appContext: Context
            private set
    }
}