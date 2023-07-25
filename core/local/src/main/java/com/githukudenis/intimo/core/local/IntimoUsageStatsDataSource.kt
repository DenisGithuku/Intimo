package com.githukudenis.intimo.core.local

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class IntimoUsageStatsDataSource @Inject constructor(
    private val usageStatsManager: UsageStatsManager
) {
    suspend fun queryAndAggregateUsageStats(
        beginTime: Long,
        endTime: Long
    ): Flow<Map<String, UsageStats>> {
        return flow {
            try {
                val stats = usageStatsManager.queryAndAggregateUsageStats(beginTime, endTime)
                emit(stats)
            } catch (e: IOException) {
                Log.e("Stats Error", e.localizedMessage ?: "Error reading stats")
                emit(emptyMap())
            }
        }
    }
}