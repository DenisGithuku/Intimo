package com.githukudenis.data.di

import com.githukudenis.data.repository.IntimoUserDataRepository
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
}