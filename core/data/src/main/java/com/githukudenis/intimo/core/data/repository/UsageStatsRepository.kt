package com.githukudenis.intimo.core.data.repository

import com.githukudenis.intimo.core.model.ApplicationInfoData
import com.githukudenis.intimo.core.model.DataUsageStats
import com.githukudenis.intimo.core.model.DayAndNotifications
import com.githukudenis.intimo.core.model.NotificationPosted
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface UsageStatsRepository {

    val dayAndNotificationList: Flow<List<DayAndNotifications>>

    val allNotificationsPosted: Flow<List<NotificationPosted>>

    fun getNotificationsByPackage(packageName: String): Flow<List<NotificationPosted>>

    suspend fun insertNotification(notificationPosted: NotificationPosted): Long

    suspend fun insertDayAndNotifications(dayId: Long, notifId: Long)

    fun queryAndAggregateUsageStats(
        date: LocalDate
    ): Flow<DataUsageStats>
    fun getIndividualAppUsage(
        packageName: String
    ): Flow<ApplicationInfoData>
}