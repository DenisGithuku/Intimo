package com.githukudenis.intimo.core.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideIntimoDatabase(
        @ApplicationContext context: Context
    ): IntimoDatabase {
        return Room.databaseBuilder(
            context,
            IntimoDatabase::class.java,
            "intimo_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}