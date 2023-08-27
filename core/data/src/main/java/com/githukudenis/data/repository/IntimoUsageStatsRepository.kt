package com.githukudenis.data.repository

import com.githukudenis.intimo.core.local.IntimoUsageStatsDataSource
import com.githukudenis.model.ApplicationInfoData
import com.githukudenis.model.DataUsageStats
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class IntimoUsageStatsRepository @Inject constructor(
    private val intimoUsageStatsDataSource: IntimoUsageStatsDataSource
) : UsageStatsRepository {
    override fun queryAndAggregateUsageStats(
        date: LocalDate
    ): Flow<DataUsageStats> {
        return intimoUsageStatsDataSource.queryAndAggregateUsageStats(date)
    }

    override fun getIndividualAppUsage(
        packageName: String
    ): Flow<ApplicationInfoData> {
        return intimoUsageStatsDataSource.getIndividualAppUsage(
            packageName
        )
    }
}