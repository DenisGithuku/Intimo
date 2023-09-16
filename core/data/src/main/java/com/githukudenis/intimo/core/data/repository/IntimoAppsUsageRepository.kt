package com.githukudenis.intimo.core.data.repository

import com.githukudenis.intimo.core.data.di.IntimoCoroutineDispatcher
import com.githukudenis.intimo.core.database.AppsInFocusModeDatasource
import com.githukudenis.intimo.core.model.AppInFocusMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class IntimoAppsUsageRepository @Inject constructor(
    private val appsInFocusModeDatasource: AppsInFocusModeDatasource,
    private val intimoCoroutineDispatcher: IntimoCoroutineDispatcher
): AppsUsageRepository {
    override val appsInFocusMode: Flow<List<AppInFocusMode>>
        get() = appsInFocusModeDatasource.appsInFocusModeList.flowOn(intimoCoroutineDispatcher.ioDispatcher)

    override suspend fun addAppToFocusMode(appInFocusMode: AppInFocusMode) {
        withContext(intimoCoroutineDispatcher.ioDispatcher) {
            appsInFocusModeDatasource.insertApp(appInFocusMode)
        }
    }

    override suspend fun deleteAppFromFocusMode(appInFocusMode: AppInFocusMode) {
        withContext(intimoCoroutineDispatcher.ioDispatcher) {
            appsInFocusModeDatasource.deleteApp(appInFocusMode)
        }
    }

    override suspend fun updateAppInFocusMode(appInFocusMode: AppInFocusMode) {
        withContext(intimoCoroutineDispatcher.ioDispatcher) {
            appsInFocusModeDatasource.updateApp(appInFocusMode)
        }
    }
}