package com.githukudenis.data.repository

import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import com.githukudenis.data.di.IntimoCoroutineDispatcher
import com.githukudenis.intimo.core.local.IntimoUsageStatsDataSource
import com.githukudenis.model.ApplicationInfoData
import com.githukudenis.model.DataUsageStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@RequiresApi(VERSION_CODES.O)
class IntimoUsageStatsRepository @Inject constructor(
    private val intimoUsageStatsDataSource: IntimoUsageStatsDataSource
) : UsageStatsRepository {
    override fun queryAndAggregateUsageStats(
        date: LocalDate
    ): Flow<DataUsageStats> {
            return intimoUsageStatsDataSource.queryAndAggregateUsageStats(date)
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