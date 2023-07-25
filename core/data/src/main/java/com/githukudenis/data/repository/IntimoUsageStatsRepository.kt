package com.githukudenis.data.repository

import android.app.usage.UsageStats
import com.githukudenis.data.di.IntimoCoroutineDispatcher
import com.githukudenis.intimo.core.local.IntimoUsageStatsDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class IntimoUsageStatsRepository @Inject constructor(
    private val intimoUsageStatsDataSource: IntimoUsageStatsDataSource,
    private val intimoCoroutineDispatcher: IntimoCoroutineDispatcher
) : UsageStatsRepository {
    override suspend fun queryAndAggregateUsageStats(
        beginTime: Long,
        endTime: Long
    ): Flow<Map<String, UsageStats>> {
        return withContext(intimoCoroutineDispatcher.ioDispatcher) {
            intimoUsageStatsDataSource.queryAndAggregateUsageStats(beginTime, endTime)
        }
    }
}