package com.githukudenis.intimo.core.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Named("test_db")
    fun provideInMemoryDb(
        @ApplicationContext context: Context
    ): IntimoDatabase = Room
        .inMemoryDatabaseBuilder(
            context,
            IntimoDatabase::class.java
        )
        .allowMainThreadQueries()
        .build()
}