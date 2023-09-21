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

    override suspend fun queryAndAggregateUsageStats(
        startDate: LocalDate,
        endDate: LocalDate
    ): DataUsageStats {
        return withContext(intimoCoroutineDispatcher.ioDispatcher) { intimoUsageStatsDataSource.queryAndAggregateUsageStats(startDate, endDate) }
    }

    override suspend fun getIndividualAppUsage(
        packageName: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): ApplicationInfoData {
        return withContext(intimoCoroutineDispatcher.ioDispatcher) {
            intimoUsageStatsDataSource.getIndividualAppUsage(
                packageName,
                startDate, endDate
            )
        }
    }

    override suspend fun getTotalWeeklyUsage(startDate: LocalDate, endDate: LocalDate): Long {
        return withContext(intimoCoroutineDispatcher.ioDispatcher) {
            intimoUsageStatsDataSource.getTotalWeeklyUsage(startDate, endDate)
        }
    }
}