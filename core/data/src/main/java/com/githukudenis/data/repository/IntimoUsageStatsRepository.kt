package com.githukudenis.data.repository

import com.githukudenis.data.di.IntimoCoroutineDispatcher
import com.githukudenis.intimo.core.local.IntimoUsageStatsDataSource
import com.githukudenis.model.ApplicationInfoData
import com.githukudenis.model.DataUsageStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class IntimoUsageStatsRepository @Inject constructor(
    private val intimoUsageStatsDataSource: IntimoUsageStatsDataSource
) : UsageStatsRepository {
    override fun queryAndAggregateUsageStats(
        beginTime: Long,
        endTime: Long,
    ): Flow<DataUsageStats> {
            return intimoUsageStatsDataSource.queryAndAggregateUsageStats(beginTime, endTime)
    }

    override fun getIndividualAppUsage(
        startTimeMillis: Long,
        endTimeMillis: Long,
        packageName: String
    ): Flow<ApplicationInfoData> {
            return intimoUsageStatsDataSource.getIndividualAppUsage(
                startTimeMillis,
                endTimeMillis,
                packageName
            )
    }
}