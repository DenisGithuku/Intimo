package com.githukudenis.intimo.core.database

import com.githukudenis.intimo.core.model.AppInFocusMode
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppsInFocusModeDatasource @Inject constructor(
    private val appsInFocusModeDao: AppsInFocusModeDao
) {
    val appsInFocusModeList: Flow<List<AppInFocusMode>>
        get() = appsInFocusModeDao.getAllAppsInFocusMode()

    fun insertApp(appInFocusMode: AppInFocusMode) {
        appsInFocusModeDao.insertAppInFocusMode(appInFocusMode)
    }

    fun deleteApp(appInFocusMode: AppInFocusMode) {
        appsInFocusModeDao.deleteAppFromFocusMode(appInFocusMode)
    }

    fun updateApp(appInFocusMode: AppInFocusMode) {
        appsInFocusModeDao.updateAppInFocusMode(appInFocusMode)
    }
}