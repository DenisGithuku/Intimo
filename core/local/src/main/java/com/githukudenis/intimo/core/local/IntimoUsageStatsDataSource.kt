package com.githukudenis.intimo.core.local

import android.app.KeyguardManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.githukudenis.model.ApplicationInfoData
import com.githukudenis.model.DataUsageStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class IntimoUsageStatsDataSource @Inject constructor(
    private val usageStatsManager: UsageStatsManager,
    private val context: Context
) {
    suspend fun queryAndAggregateUsageStats(
        beginTime: Long,
        endTime: Long
    ): Flow<DataUsageStats> {
        return flow {
            val allEvents = mutableListOf<UsageEvents.Event>()

            /* Map with app package name as the key */
            val appUsageInfoMap = hashMapOf<String, ApplicationInfoData>()

            val usageStats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST,
                beginTime, endTime
            )

            /* Query installed apps */
            val installedApps =
                context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

            /*
            * Transform list to sequence for minimal operation
            * filter out apps that are not installed and non system apps
            *  */
            val usageList = usageStats
                .asSequence()
                .filter { appUsageStats ->
                    appUsageStats.packageName in installedApps.map { app -> app.packageName } &&
                            isNonSystemApp(appUsageStats.packageName)
                }
                .map { appUsageStats ->
                    ApplicationInfoData(
                        packageName = appUsageStats.packageName,
                        usageDuration = appUsageStats.totalTimeInForeground,
                        usagePercentage = getAppUsagePercentage(appUsageStats.totalTimeInForeground, usageStats)
                    )
                }
                .sortedByDescending { applicationInfoData ->
                    applicationInfoData.usageDuration
                }
                .toList()

            var unlockCount = 0
            val keyguardManager =
                context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager


            val systemEvents = if (!keyguardManager.isKeyguardLocked) {
                usageStatsManager.queryEvents(beginTime, endTime)
            } else {
                null
            }

            while (systemEvents?.hasNextEvent() == true) {
                val currentEvent = UsageEvents.Event()
                systemEvents.getNextEvent(currentEvent)

//                if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED ||
//                    currentEvent.eventType == UsageEvents.Event.ACTIVITY_PAUSED
//                ) {
//                    allEvents.add(currentEvent)
//
//                    val key = currentEvent.packageName
//                    if (appUsageInfoMap[key] == null) {
//                        appUsageInfoMap[key] = ApplicationInfoData(packageName = key)
//                    }
//                }


                if (currentEvent.eventType == UsageEvents.Event.KEYGUARD_HIDDEN) {
                    unlockCount++
                }
            }

            for (i in 0 until allEvents.lastIndex) {
                val currEvent = allEvents[i]
                val nextEvent = allEvents[i + 1]

                /* Generate checks */
//                val timeInMillis = System.currentTimeMillis()
//                val eventsFromSameApp = currEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED &&
//                        nextEvent.eventType == UsageEvents.Event.ACTIVITY_PAUSED &&
//                        currEvent.className == nextEvent.className
//
//                val appIsActive = (i + 1 == allEvents.lastIndex) &&
//                        (nextEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED) &&
//                        (beginTime..endTime).contains(timeInMillis)
//
//                if (eventsFromSameApp || appIsActive) {
//                    val timeDifference = if (appIsActive) {
//                        timeInMillis - nextEvent.timeStamp
//                    } else {
//                        nextEvent.timeStamp - currEvent.timeStamp
//                    }
//                    appUsageInfoMap[nextEvent.packageName] =
//                        appUsageInfoMap[nextEvent.packageName]!!.copy(
//                            usageDuration = appUsageInfoMap[nextEvent.packageName]!!.usageDuration + timeDifference
//                        )
//                }

                /* calculate app launch count */
                if (currEvent.packageName != nextEvent.packageName &&
                    nextEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED
                ) {
                    /* Different app was launched and activity is in resumed state */
                    appUsageInfoMap[nextEvent.packageName] =
                        appUsageInfoMap[nextEvent.packageName]!!.copy(
                            appLaunchCount = appUsageInfoMap[nextEvent.packageName]!!.appLaunchCount + 1
                        )
                }
            }

            /* Filter out system apps
            - app is system if it does not have a launch intent
             */
//            val appUsageInfoList = appUsageInfoMap.values.filter { app ->
//                context.packageManager.getLaunchIntentForPackage(app.packageName) != null
//            }.sortedByDescending { app ->
//                app.usagePercentage
//            }

            /* Get percentage and icon for each application */
//            appUsageInfoList.forEach { applicationInfoData ->
//                applicationInfoData.icon = getAppIcon(applicationInfoData.packageName)
//                applicationInfoData.usagePercentage = getAppUsagePercentage(
//                    individualAppUsage = applicationInfoData.usageDuration,
//                    appUsageList = appUsageInfoList
//                )
//            }
            emit(DataUsageStats(appUsageList = usageList, unlockCount = unlockCount))
        }
    }

    suspend fun getIndividualAppUsage(
        startTimeMillis: Long,
        endTimeMillis: Long,
        packageName: String
    ): Flow<ApplicationInfoData> {

        val appUsageList = queryAndAggregateUsageStats(
            beginTime = startTimeMillis,
            endTime = endTimeMillis
        )

        return appUsageList.map { usageStats ->
            usageStats.appUsageList.firstOrNull { app -> app.packageName == packageName }
                ?: ApplicationInfoData(packageName = packageName)
        }
    }

    private fun getAppUsagePercentage(
        individualAppUsage: Long,
        appUsageList: List<UsageStats>
    ): Float {
        val totalDuration = appUsageList.map {
            it.totalTimeInForeground.toFloat()
        }.sum()

        return (individualAppUsage * 100) / totalDuration
    }

    private fun isNonSystemApp(packageName: String): Boolean {
        val packageManager = context.packageManager
        return packageManager.getLaunchIntentForPackage(packageName) != null
    }
}