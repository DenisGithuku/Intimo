package com.githukudenis.data.repository

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.githukudenis.model.NotificationPosted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class IntimoNotificationsListener : NotificationListenerService() {

    @Inject
    lateinit var usageStatsRepository: UsageStatsRepository

    val dayId = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private var scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn?.let { statusBarNotification ->
            scope.launch {
                usageStatsRepository.insertNotification(
                    NotificationPosted(
                        notificationId = statusBarNotification.id,
                        packageName = statusBarNotification.packageName
                    )
                )
                usageStatsRepository.insertDayAndNotifications(
                    dayId = dayId,
                    notifId = statusBarNotification.id.toLong()
                )
            }
        }
    }
}