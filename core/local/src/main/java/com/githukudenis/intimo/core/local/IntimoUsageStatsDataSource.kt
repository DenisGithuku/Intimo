package com.githukudenis.intimo.core.local

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager.NameNotFoundException
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class IntimoUsageStatsDataSource @Inject constructor(
    private val usageStatsManager: UsageStatsManager,
    private val context: Context
) {
    suspend fun queryAndAggregateUsageStats(
        beginTime: Long,
        endTime: Long
    ): Flow<Map<String, UsageStats>> {
        return flow {
            try {
                /* query all system info */
                val stats = usageStatsManager.queryAndAggregateUsageStats(beginTime, endTime)

                /* create an empty map to hold non system apps */
                val nonSystemApps = mutableMapOf<String, UsageStats>()

                /* filter out system apps */
                val packageManager = context.packageManager
                stats.forEach { (packageName, usageStats) ->
                    try {
                        /* App is non system if it can be launched via an intent */

                        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)

                        val isNonSystem =
                            launchIntent != null

                        if (isNonSystem) {
                            nonSystemApps[packageName] = usageStats
                        }
                    } catch (e: NameNotFoundException) {
                        Log.e(
                            "Stats Error name",
                            e.localizedMessage ?: "Could not find application"
                        )
                    }
                }
                emit(nonSystemApps)
            } catch (e: IOException) {
                Log.e("Stats Error", e.localizedMessage ?: "Error reading stats")
                emit(emptyMap())
            }
        }
    }
}