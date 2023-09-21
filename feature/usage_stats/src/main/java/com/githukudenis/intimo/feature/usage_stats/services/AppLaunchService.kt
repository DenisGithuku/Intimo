package com.githukudenis.intimo.feature.usage_stats.services

import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.githukudenis.intimo.core.data.repository.AppsUsageRepository
import com.githukudenis.intimo.feature.usage_stats.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

const val appLaunchNotificationId = 10101

@AndroidEntryPoint
class AppLaunchService : Service() {

    @Inject
    lateinit var appsUsageRepository: AppsUsageRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val usageStatsManager: UsageStatsManager by lazy {
        getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
    }

    private val notificationManager: NotificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createAppLaunchNotification(this@AppLaunchService)

        notificationManager.notify(
            appLaunchNotificationId,
            notification.build()
        )
        startMonitoring(notification)

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("app launch service", "app launch service started")
    }

    private fun startMonitoring(notification: NotificationCompat.Builder) {
        Log.d("app launch service", "start monitoring started")
        scope.launch {
            val now = System.currentTimeMillis()
            val systemEvents = usageStatsManager.queryEvents(now - 1000 * 60, now)
            while (systemEvents.hasNextEvent()) {
                val event = UsageEvents.Event()
                systemEvents.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    val limitReached = appsUsageRepository.appsInFocusMode.first()
                        .find { appInFocusMode -> appInFocusMode.packageName == event.packageName }?.limitReached == true
                    Log.d("limit", "${event.packageName} $limitReached")
                    if (limitReached) {
                        notificationManager.notify(
                            appLaunchNotificationId,
                            notification.apply {
                                setContentText(
                                    getString(
                                        R.string.app_limit_reached,
                                        packageManager.getApplicationLabel(
                                            packageManager.getApplicationInfo(
                                                event.packageName,
                                                PackageManager.GET_META_DATA
                                            )
                                        )
                                    )
                                )
                            }.build()
                        )
                    }
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        notificationManager.cancel(appLaunchNotificationId)
    }

    private fun createAppLaunchNotification(context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, "intimo_notifications")
            .setSmallIcon(com.githukudenis.intimo.core.designsystem.R.drawable.intimologo)
            .setContentTitle("App usage monitor")
            .setContentText("Practise intentional device usage")
            .setOngoing(true)
    }

}