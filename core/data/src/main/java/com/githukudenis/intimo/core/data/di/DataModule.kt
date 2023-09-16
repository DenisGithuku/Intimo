package com.githukudenis.intimo.core.data.di

import com.githukudenis.intimo.core.data.repository.AppsUsageRepository
import com.githukudenis.intimo.core.data.repository.HabitsRepository
import com.githukudenis.intimo.core.data.repository.IntimoAppsUsageRepository
import com.githukudenis.intimo.core.data.repository.IntimoHabitsRepository
import com.githukudenis.intimo.core.data.repository.IntimoUsageStatsRepository
import com.githukudenis.intimo.core.data.repository.IntimoUserDataRepository
import com.githukudenis.intimo.core.data.repository.UsageStatsRepository
import com.githukudenis.intimo.core.data.repository.UserDataRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun bindsUserDataRepository(
        userDataRepository: IntimoUserDataRepository
    ): UserDataRepository

    @Binds
    fun bindsUsageStatsRepository(
        usageStatsRepository: IntimoUsageStatsRepository
    ): UsageStatsRepository

    @Binds
    fun bindsHabitsRepository(
        habitsRepository: IntimoHabitsRepository
    ): HabitsRepository

    @Binds
    fun bindIntimoAppsUsageRepository(
        appsUsageRepository: IntimoAppsUsageRepository
    ): AppsUsageRepository
}