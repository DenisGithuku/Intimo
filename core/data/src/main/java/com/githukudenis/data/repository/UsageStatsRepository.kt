package com.githukudenis.data.repository

import com.githukudenis.model.ApplicationInfoData
import com.githukudenis.model.DataUsageStats
import kotlinx.coroutines.flow.Flow

interface UsageStatsRepository {

    suspend fun queryAndAggregateUsageStats(
        beginTime: Long,
        endTime: Long,
    ): Flow<DataUsageStats>

    suspend fun getIndividualAppUsage(
        startTimeMillis: Long,
        endTimeMillis: Long,
        packageName: String
    ): Flow<ApplicationInfoData>
}