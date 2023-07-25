package com.githukudenis.data.repository

import android.app.usage.UsageStats
import kotlinx.coroutines.flow.Flow

interface UsageStatsRepository {

    suspend fun queryAndAggregateUsageStats(
        beginTime: Long = System.currentTimeMillis() - 24 * 60 * 60 * 1000,
        endTime: Long = System.currentTimeMillis()
    ): Flow<Map<String, UsageStats>>
}