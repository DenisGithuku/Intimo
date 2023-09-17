package com.githukudenis.intimo.core.data.repository

import com.githukudenis.intimo.core.datastore.IntimoPrefsDataSource
import com.githukudenis.intimo.core.model.Theme
import com.githukudenis.intimo.core.model.UserData
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

    override suspend fun setShouldAllowDeviceNotifications(shouldAllowDeviceNotifications: Boolean) {
        intimoPrefsDataSource.setShouldAllowDeviceNotifications(shouldAllowDeviceNotifications)
    }

    override suspend fun setShouldAllowHabitNotifications(shouldAllowHabitNotifications: Boolean) {
        intimoPrefsDataSource.setShouldAllowHabitNotifications(shouldAllowHabitNotifications)
    }

    override suspend fun setAppTheme(theme: Theme) {
        intimoPrefsDataSource.setAppTheme(theme)
    }
}