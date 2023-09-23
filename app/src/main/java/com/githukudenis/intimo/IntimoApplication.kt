package com.githukudenis.intimo

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.githukudenis.intimo.core.data.util.DataSyncManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


const val activeHabitNotificationChannelId = "active_habit_notifs"
const val activeHabitNotificationChannelName = "Active Habit"

const val habitRemindersNotificationChannelId = "habit_reminders_notifs"
const val habitRemindersNotificationChannelName = "Periodic Habit Alerts"

@HiltAndroidApp
class IntimoApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        setUpDataSyncManager()

        setupNotificationChannel()
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

        val activeHabitChannel = NotificationChannel(
            activeHabitNotificationChannelId,
            activeHabitNotificationChannelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            setSound(notificationSound, audioAttributes)
            setShowBadge(true)
            description = getString(R.string.intimo_alerts_description)
        }

        val habitRemindersChannel = NotificationChannel(
            habitRemindersNotificationChannelId,
            habitRemindersNotificationChannelName,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            setShowBadge(true)
            description = getString(R.string.periodic_habit_alerts_description)
        }

        notificationManager.createNotificationChannel(activeHabitChannel)
        notificationManager.createNotificationChannel(habitRemindersChannel)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}