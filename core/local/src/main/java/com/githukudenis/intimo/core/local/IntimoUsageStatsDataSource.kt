package com.githukudenis.intimo.core.local

import android.app.KeyguardManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.graphics.Bitmap
import android.hardware.display.DisplayManager
import android.util.Log
import android.view.Display
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import com.githukudenis.intimo.core.database.DayAndNotificationsDao
import com.githukudenis.intimo.core.database.NotificationsDao
import com.githukudenis.intimo.core.model.ApplicationInfoData
import com.githukudenis.intimo.core.model.DataUsageStats
import com.githukudenis.intimo.core.model.DayAndNotifications
import com.githukudenis.intimo.core.model.DayAndNotificationsPostedCrossRef
import com.githukudenis.intimo.core.model.NotificationPosted
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class IntimoUsageStatsDataSource @Inject constructor(
    private val usageStatsManager: UsageStatsManager,
    private val dayAndNotificationsDao: DayAndNotificationsDao,
    private val notificationsDao: NotificationsDao,
    private val context: Context
) {
    val allNotificationsPosted: Flow<List<NotificationPosted>>
        get() = notificationsDao.getAllNotifications()
    val notificationPostedData: Flow<List<DayAndNotifications>>
        get() = dayAndNotificationsDao.getDayAndNotifications()

    suspend fun queryAndAggregateUsageStats(
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate = LocalDate.now()
    ): DataUsageStats {
        // set id to utc - api works with utc
        val utc = ZoneId.of("UTC")
        val defaultZone = ZoneId.systemDefault()

        // set the and end time to utc midnight time
        val start = startDate.atStartOfDay(defaultZone).withZoneSameInstant(utc)
            .toInstant().toEpochMilli()
        val end = endDate.atStartOfDay(defaultZone).withZoneSameInstant(utc).plusDays(1).toInstant()
            .toEpochMilli()

        var unlockCount = 0

        //all events
        val allEvents = mutableListOf<UsageEvents.Event>()

        // key set to package name
        val appUsageInfoMap = hashMapOf<String, ApplicationInfoData>()


        val keyguardManager =
            context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        //query events only if device is unlocked
        val systemEvents = if (!keyguardManager.isKeyguardLocked) {
            usageStatsManager.queryEvents(start, end)
        } else {
            null
        }

        while (systemEvents?.hasNextEvent() == true) {
            val event = UsageEvents.Event()
            systemEvents.getNextEvent(event)

            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED ||
                event.eventType == UsageEvents.Event.ACTIVITY_PAUSED
            ) {
                allEvents.add(event)

                try {
                    // key set to package name
                    val key = event.packageName
                    if (appUsageInfoMap[key] == null) {
                        appUsageInfoMap[key] = ApplicationInfoData(
                            packageName = key,
                            colorSwatch = createPaletteAsync(
                                context.packageManager.getApplicationIcon(key).toBitmap()
                            ),
                            icon = context.packageManager.getApplicationIcon(key)
                        )
                    }
                } catch (e: NameNotFoundException) {
                    Log.e("app insertion error", e.message, e)
                }
            }

            // get unlock count
            if (event.eventType == UsageEvents.Event.KEYGUARD_HIDDEN) {
                unlockCount++
            }

        }
        for (i in 0 until allEvents.lastIndex) {
            val e0 = allEvents[i]
            val e1 = allEvents[i + 1]

            // generate checks
            val currentTime = System.currentTimeMillis()
            val eventsFromSameApp = e0.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND &&
                    e1.eventType == UsageEvents.Event.MOVE_TO_BACKGROUND &&
                    e0.packageName == e1.packageName
            val appIsActive = (1 + i == allEvents.lastIndex) &&
                    (e0.eventType == UsageEvents.Event.MOVE_TO_BACKGROUND) &&
                    (start..end).contains(currentTime)

            // app foreground usage
            if (appUsageInfoMap[e1.packageName] != null) {
                if (eventsFromSameApp || appIsActive) {
                    UsageEvents.Event.USER_INTERACTION
                    val diff = if (appIsActive) {
                        currentTime - e1.timeStamp
                    } else {
                        e1.timeStamp - e0.timeStamp
                    }
                    appUsageInfoMap[e1.packageName]!!.usageDuration += diff
                }

                // app launch count
                if (e0.packageName != e1.packageName && e1.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                    appUsageInfoMap[e1.packageName]!!.appLaunchCount++
                }
            }
        }

        val appInfoUsageList = appUsageInfoMap.values.asSequence()
            .filter {
                isNonSystemApp(packageName = it.packageName) &&
                        isInstalled(packageName = it.packageName)
            }
            .sortedByDescending {
                it.usageDuration
            }.toMutableList()

        return DataUsageStats(
            appUsageList = appInfoUsageList.toImmutableList(),
            unlockCount = unlockCount
        )
    }


    suspend fun getTotalWeeklyUsage(
        startDate: LocalDate,
        endDate: LocalDate = LocalDate.now()
    ): Long {
        val allStats = queryAndAggregateUsageStats(
            startDate, endDate
        )
        return allStats.appUsageList.sumOf { it.usageDuration }
    }

    suspend fun getIndividualAppUsage(
        packageName: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): ApplicationInfoData {
        return queryAndAggregateUsageStats(startDate, endDate)
            .appUsageList.first { it.packageName == packageName }
    }

    private fun isNonSystemApp(packageName: String): Boolean {
        val packageManager = context.packageManager
        return packageManager.getLaunchIntentForPackage(packageName) != null
    }

    private fun isInstalled(packageName: String): Boolean {
        val installedApps =
            context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        return installedApps.any {
            it.packageName == packageName
        }
    }

    private fun isScreenOn(): Boolean {
        val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        return displayManager.getDisplay(Display.DEFAULT_DISPLAY).state == Display.STATE_ON
    }

    private suspend fun createPaletteAsync(bitmap: Bitmap): Int? {
        return suspendCoroutine { continuation ->
            Palette.from(bitmap).generate { palette ->
                val swatch = palette?.dominantSwatch?.rgb
                continuation.resume(swatch)
            }
        }
    }

    fun insertDayAndNotifications(dayId: Long, notifId: Long) {
        dayAndNotificationsDao.insertDayAndNotification(
            DayAndNotificationsPostedCrossRef(
                dayId = dayId,
                notifPrimaryId = notifId
            )
        )
    }

    fun getNotificationsByPackage(packageName: String): Flow<List<NotificationPosted>> {
        return notificationsDao.getNotificationsByPackage(packageName)
    }

    fun insertNotificationPosted(notificationPosted: NotificationPosted): Long {
        return notificationsDao.insertNotification(notificationPosted)
    }
}