package com.githukudenis.intimo.core.database.di

import android.content.Context
import androidx.room.Room
import com.githukudenis.intimo.core.database.DayAndHabitsDao
import com.githukudenis.intimo.core.database.DayDao
import com.githukudenis.intimo.core.database.HabitDao
import com.githukudenis.intimo.core.database.IntimoDatabase
import com.githukudenis.intimo.core.database.IntimoHabitsDataSource
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

    @Provides
    @Singleton
    fun provideHabitDao(
        intimoDatabase: IntimoDatabase
    ): HabitDao = intimoDatabase.habitDao()

    @Provides
    @Singleton
    fun provideDayDao(
        intimoDatabase: IntimoDatabase
    ): DayDao = intimoDatabase.dayDao()

    @Provides
    @Singleton
    fun provideDayAndHabitsDao(
        intimoDatabase: IntimoDatabase
    ): DayAndHabitsDao = intimoDatabase.dayAndHabitsDao()

    @Provides
    @Singleton
    fun provideHabitsDataSource(
        habitDao: HabitDao,
        dayDao: DayDao,
        dayAndHabitsDao: DayAndHabitsDao,
    ): IntimoHabitsDataSource {
        return IntimoHabitsDataSource(
            habitDao = habitDao,
            dayDao = dayDao,
            dayAndHabitsDao = dayAndHabitsDao,
        )
    }
}