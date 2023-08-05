package com.githukudenis.data.di

import com.githukudenis.data.repository.HabitsRepository
import com.githukudenis.data.repository.IntimoHabitsRepository
import com.githukudenis.data.repository.IntimoUsageStatsRepository
import com.githukudenis.data.repository.IntimoUserDataRepository
import com.githukudenis.data.repository.UsageStatsRepository
import com.githukudenis.data.repository.UserDataRepository
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
}