package com.githukudenis.data.repository

import com.githukudenis.model.ApplicationInfoData
import com.githukudenis.model.DataUsageStats
import com.githukudenis.model.DayAndNotifications
import com.githukudenis.model.NotificationPosted
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