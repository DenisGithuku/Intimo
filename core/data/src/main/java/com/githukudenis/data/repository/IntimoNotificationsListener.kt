package com.githukudenis.data.repository

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.githukudenis.data.di.IntimoCoroutineDispatcher
import com.githukudenis.datastore.IntimoPrefsDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class IntimoNotificationsListener: NotificationListenerService() {

    @Inject
    lateinit var intimoPrefsDataSource: IntimoPrefsDataSource

    @Inject
    lateinit var intimoCoroutineDispatcher: IntimoCoroutineDispatcher

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val scope = CoroutineScope(SupervisorJob() + intimoCoroutineDispatcher.ioDispatcher)
        scope.launch {
            intimoPrefsDataSource.userData.map { it.notificationCount }.distinctUntilChanged()
                .collectLatest { notificationCount ->
                    intimoPrefsDataSource.storeNotificationCount(notificationCount + 1)
                }
        }
    }
}