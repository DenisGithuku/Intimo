package com.githukudenis.intimo

import android.app.Application
import com.githukudenis.data.util.DataSyncManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class IntimoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setUpDataSyncManager()
    }

    private fun setUpDataSyncManager() {
        val dataSyncManager = DataSyncManager()
        dataSyncManager.setupRefreshAlarm(this)
    }
}