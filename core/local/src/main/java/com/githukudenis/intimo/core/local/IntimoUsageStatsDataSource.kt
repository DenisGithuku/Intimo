package com.githukudenis.intimo.core.local

import android.app.KeyguardManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.hardware.display.DisplayManager
import android.os.Build
import android.util.Log
import android.view.Display
import androidx.annotation.RequiresApi
import com.githukudenis.model.ApplicationInfoData
import com.githukudenis.model.DataUsageStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

class IntimoUsageStatsDataSource @Inject constructor(
    private val usageStatsManager: UsageStatsManager,
    private val context: Context
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun queryAndAggregateUsageStats(
        date: LocalDate = LocalDate.now()
    ): Flow<DataUsageStats> {
        return flow {
            // set id to utc - api works with utc
            val utc = ZoneId.of("UTC")
            val defaultZone = ZoneId.systemDefault()

            // set the and end time to utc midnight time
            val startTime = date.atStartOfDay(defaultZone).withZoneSameInstant(utc)
            val start = startTime.toInstant().toEpochMilli()
            val end = startTime.plusDays(1).toInstant().toEpochMilli()

            var unlockCount = 0
            var allAppsUsageTime = 0L

            // sorted events
            val sortedEvents = mutableMapOf<String, MutableList<UsageEvents.Event>>()


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

                // get unclock count
                if (event.eventType == UsageEvents.Event.KEYGUARD_HIDDEN) {
                    unlockCount++
                }

                // get event list - create one if none exists
                val packageEvents = sortedEvents[event.packageName] ?: mutableListOf()
                packageEvents.add(event)
                sortedEvents[event.packageName] = packageEvents
            }

            Log.d("events", sortedEvents.map { it.value }.toString())

            var usageList = mutableListOf<ApplicationInfoData>()


            sortedEvents.forEach { (packageName, events) ->
                //keep track of current event start time and end times
                var eventStartTime = 0L
                var eventEndTime = 0L
                var totalTime = 0L
                var appLaunchCount = 0

                // all start times for a particular app
                var eventStartTimeList = mutableListOf<ZonedDateTime>()

                events.forEach { event ->
                    // register time when first shown
                    if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND && isScreenOn()) {


                        eventStartTime = event.timeStamp
                        appLaunchCount += 1
                        eventStartTimeList.add(
                            Instant.ofEpochMilli(eventStartTime).atZone(defaultZone)
                                .withZoneSameInstant(defaultZone)
                        )


                    } else if (event.eventType == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                        eventEndTime = event.timeStamp
                    }

                    /* if there's an end time and no start time
                    then the app was started the previous day
                    register midnight as the start time
                     */
                    if (eventStartTime == 0L && eventEndTime != 0L) {
                        eventStartTime = start
                    }

                    /*
                    Both start and end times are defined - this
                    means we have a session
                     */
                    if (eventStartTime != 0L && eventEndTime != 0L) {
                        // add session to total time
                        totalTime += eventEndTime - eventStartTime
                        allAppsUsageTime += totalTime
                        // reset start and end times
                        eventStartTime = 0L
                        eventEndTime = 0L
                        appLaunchCount = 0
                    }
                }

                usageList.add(
                    ApplicationInfoData(
                        packageName = packageName,
                        icon = getApplicationIcon(packageName),
                        usageDuration = totalTime,
                        appLaunchCount = appLaunchCount
                    )
                )
            }

            usageList = usageList.asSequence()
                .filter {
                    isNonSystemApp(packageName = it.packageName) &&
                            isInstalled(packageName = it.packageName)
                }
                .sortedByDescending {
                    it.usageDuration
                }
                .toMutableList()

            emit(DataUsageStats(appUsageList = usageList, unlockCount = unlockCount))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getIndividualAppUsage(
        startTimeMillis: Long,
        endTimeMillis: Long,
        packageName: String
    ): Flow<ApplicationInfoData> {

        val appUsageList = queryAndAggregateUsageStats()

        return appUsageList.map { usageStats ->
            usageStats.appUsageList.firstOrNull { app -> app.packageName == packageName }
                ?: ApplicationInfoData(packageName = packageName)
        }
    }

    private fun getAppUsagePercentage(
        individualUsage: Long,
        totalTime: Long
    ): Float {
        return ((individualUsage / totalTime) * 100).toFloat()
    }

    private fun getApplicationIcon(packageName: String): Drawable {
        return context.packageManager.getApplicationIcon(packageName)
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
}