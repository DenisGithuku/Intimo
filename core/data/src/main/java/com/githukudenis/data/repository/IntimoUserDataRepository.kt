package com.githukudenis.data.repository

import com.githukudenis.datastore.IntimoPrefsDataSource
import com.githukudenis.model.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IntimoUserDataRepository @Inject constructor(
    private val intimoPrefsDataSource: IntimoPrefsDataSource
) : UserDataRepository {
    override val userData: Flow<UserData>
        get() = intimoPrefsDataSource.userData

    override suspend fun setShouldHideOnBoarding(shouldHideOnBoarding: Boolean) {
        intimoPrefsDataSource.setShouldHideOnBoarding(shouldHideOnBoarding)
    }

    override suspend fun updateHabitTime(habitTime: Long) {
        intimoPrefsDataSource.setHabitTime(habitTime)
    }
}