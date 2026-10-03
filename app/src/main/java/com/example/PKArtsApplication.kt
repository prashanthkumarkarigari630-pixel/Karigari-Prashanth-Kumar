package com.example

import android.app.Application
import com.example.data.AppDatabase
import com.example.util.NotificationHelper

class PKArtsApplication : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannels(this)
    }
}
