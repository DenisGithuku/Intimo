package com.githukudenis.intimo.core.data.repository

import com.githukudenis.intimo.core.data.di.IntimoCoroutineDispatcher
import com.githukudenis.intimo.core.local.IntimoUsageStatsDataSource
import com.githukudenis.intimo.core.model.ApplicationInfoData
import com.githukudenis.intimo.core.model.DataUsageStats
import com.githukudenis.intimo.core.model.DayAndNotifications
import com.githukudenis.intimo.core.model.NotificationPosted
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

class IntimoUsageStatsRepository @Inject constructor(
    private val intimoUsageStatsDataSource: IntimoUsageStatsDataSource,
    private val intimoCoroutineDispatcher: IntimoCoroutineDispatcher
) : UsageStatsRepository {
    override val dayAndNotificationList: Flow<List<DayAndNotifications>>
        get() = intimoUsageStatsDataSource.notificationPostedData.flowOn(intimoCoroutineDispatcher.ioDispatcher)
    override val allNotificationsPosted: Flow<List<NotificationPosted>>
        get() = intimoUsageStatsDataSource.allNotificationsPosted.flowOn(intimoCoroutineDispatcher.ioDispatcher)

    override fun getNotificationsByPackage(packageName: String): Flow<List<NotificationPosted>> {
            return intimoUsageStatsDataSource.getNotificationsByPackage(packageName).flowOn(intimoCoroutineDispatcher.ioDispatcher)
    }

    override suspend fun insertNotification(notificationPosted: NotificationPosted): Long {
        return withContext(intimoCoroutineDispatcher.ioDispatcher) {
            intimoUsageStatsDataSource.insertNotificationPosted(notificationPosted)
        }
    }

    override suspend fun insertDayAndNotifications(dayId: Long, notifId: Long) {
        withContext(intimoCoroutineDispatcher.ioDispatcher) {
            intimoUsageStatsDataSource.insertDayAndNotifications(dayId, notifId)
        }
    }

    override fun queryAndAggregateUsageStats(
        date: LocalDate
    ): Flow<DataUsageStats> {
        return intimoUsageStatsDataSource.queryAndAggregateUsageStats(date)
    }

    override fun getIndividualAppUsage(
        packageName: String
    ): Flow<ApplicationInfoData> {
        return intimoUsageStatsDataSource.getIndividualAppUsage(
            packageName
        )
    }

    override fun getTotalWeeklyUsage(date: LocalDate): Flow<Long> {
        return intimoUsageStatsDataSource.getTotalWeeklyUsage(date).flowOn(intimoCoroutineDispatcher.ioDispatcher)
    }
}