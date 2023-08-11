package com.githukudenis.data.repository

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.githukudenis.data.di.IntimoCoroutineDispatcher
import com.githukudenis.datastore.IntimoPrefsDataSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class IntimoNotificationsListener : NotificationListenerService() {

    @Inject
    lateinit var intimoPrefsDataSource: IntimoPrefsDataSource

    @Inject
    lateinit var intimoCoroutineDispatcher: IntimoCoroutineDispatcher

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val scope = CoroutineScope(Job() + intimoCoroutineDispatcher.ioDispatcher)
        scope.launch {
            val notificationCount = intimoPrefsDataSource.userData.first().notificationCount
            intimoPrefsDataSource.storeNotificationCount(notificationCount + 1)
        }
    }
}