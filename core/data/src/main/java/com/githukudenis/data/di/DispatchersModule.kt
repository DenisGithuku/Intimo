package com.githukudenis.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {

    @Provides
    @Singleton
    fun provideIntimoCoroutineDispatcher(): IntimoCoroutineDispatcher {
        return IntimoCoroutineDispatcher(
            defaultDispatcher = Dispatchers.Default,
            ioDispatcher = Dispatchers.IO,
            mainDispatcher = Dispatchers.Main,
            unConfinedDispatcher = Dispatchers.Unconfined
        )
    }
}

data class IntimoCoroutineDispatcher(
    val defaultDispatcher: CoroutineDispatcher,
    val ioDispatcher: CoroutineDispatcher,
    val mainDispatcher: CoroutineDispatcher,
    val unConfinedDispatcher: CoroutineDispatcher,
)
