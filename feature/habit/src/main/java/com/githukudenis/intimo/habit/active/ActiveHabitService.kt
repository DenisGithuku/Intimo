package com.githukudenis.intimo.habit.active

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
import com.githukudenis.intimo.core.designsystem.R
import com.githukudenis.intimo.habit.components.formatCountdownTime

const val notificationId: Int = 101


class ActiveHabitService : Service() {

    private val notificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            intent.extras?.let { extras ->
                val notificationData = NotificationData(
                    title = extras.getString("title"),
                    content = extras.getString("content"),
                    duration = extras.getLong("duration")
                )

                when (action) {
                    NotificationAction.START.toString() -> start(notificationData)
                    NotificationAction.STOP.toString() -> {
                        notificationManager.cancel(notificationId)
                        stopSelf()
                    }
                }
            }
        }
        return START_STICKY
    }

    private fun start(notificationData: NotificationData) {
        val notification = createNotification(notificationData)
        notificationManager.notify(notificationId, notification.build())
        notificationData.duration?.let { duration ->
            if (duration <= 0L) {
                return
            }

            Thread.sleep(1000L)
            notification.setContentText(
                formatCountdownTime(duration)
            )
        }
        notificationManager.notify(notificationId, notification.build())
    }


    private fun createNotification(
        notificationData: NotificationData
    ): NotificationCompat.Builder {

        return NotificationCompat.Builder(this, "Habit Notification")
            .setContentTitle(notificationData.title)
            .setContentText(notificationData.content)
            .setLargeIcon(
                BitmapFactory.decodeResource(resources, R.drawable.intimologo)
            )
            .setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
            .setSmallIcon(R.drawable.intimologo)
            .setOngoing(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationManager.cancelAll()
    }


    enum class NotificationAction {
        START,
        STOP,
    }

    data class NotificationData(
        val title: String? = null,
        val content: String? = null,
        val duration: Long? = null
    )
}