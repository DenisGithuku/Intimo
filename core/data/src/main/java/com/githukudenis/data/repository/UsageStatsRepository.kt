package com.githukudenis.data.repository

import com.githukudenis.model.ApplicationInfoData
import com.githukudenis.model.DataUsageStats
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface UsageStatsRepository {

    fun queryAndAggregateUsageStats(
        date: LocalDate
    ): Flow<DataUsageStats>

    fun getIndividualAppUsage(
        startTimeMillis: Long,
        endTimeMillis: Long,
        packageName: String
    ): Flow<ApplicationInfoData>
}