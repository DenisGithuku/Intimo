package com.githukudenis.data.repository

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.githukudenis.datastore.IntimoPrefsDataSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class IntimoNotificationsListener : NotificationListenerService() {

    @Inject
    lateinit var intimoPrefsDataSource: IntimoPrefsDataSource


    private var scope = CoroutineScope(Job() + Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn?.let { statusBarNotification ->
            // Filter out unclearable and ongoing notifications
            if (!statusBarNotification.isClearable || statusBarNotification.isOngoing) {
                return
            }
            scope.launch {
                val notificationCount = intimoPrefsDataSource.userData.first().notificationCount
                intimoPrefsDataSource.storeNotificationCount(notificationCount + 1)
            }
        }
    }
}