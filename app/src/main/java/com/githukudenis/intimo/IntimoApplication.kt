package com.githukudenis.intimo

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import com.githukudenis.data.util.DataSyncManager
import dagger.hilt.android.HiltAndroidApp

const val notificationChannelId = "intimo_notifications"
const val notificationName = "Intimo Alerts"

@HiltAndroidApp
class IntimoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setUpDataSyncManager()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setupNotificationChannel()
        }
    }

    private fun setUpDataSyncManager() {
        val dataSyncManager = DataSyncManager()
        dataSyncManager.setupRefreshAlarm(this)
    }

    private fun setupNotificationChannel() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        /* custom sound for notification */
        val notificationSound = Uri.parse("android.resource://$packageName/raw/notification_sound")

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        val notificationChannel = NotificationChannel(
            notificationChannelId,
            notificationName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            setSound(notificationSound, audioAttributes)
            setShowBadge(true)
            enableVibration(true)
        }

        notificationManager.createNotificationChannel(notificationChannel)
    }
}