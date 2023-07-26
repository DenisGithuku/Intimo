package com.githukudenis.intimo.core.local.di

import android.app.usage.UsageStatsManager
import android.content.Context
import com.githukudenis.intimo.core.local.IntimoUsageStatsDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun provideUsageStatsManager(
        @ApplicationContext context: Context
    ): UsageStatsManager {
        return context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    }

    @Provides
    @Singleton
    fun provideIntimoUsageStatsDataSource(
        @ApplicationContext context: Context,
        usageStatsManager: UsageStatsManager
    ): IntimoUsageStatsDataSource {
        return IntimoUsageStatsDataSource(usageStatsManager = usageStatsManager, context = context)
    }
}