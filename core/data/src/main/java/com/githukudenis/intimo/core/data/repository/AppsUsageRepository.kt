package com.githukudenis.intimo.core.data.repository

import com.githukudenis.intimo.core.model.AppInFocusMode
import kotlinx.coroutines.flow.Flow

interface AppsUsageRepository {

    val appsInFocusMode: Flow<List<AppInFocusMode>>

    suspend fun addAppToFocusMode(appInFocusMode: AppInFocusMode)

    suspend fun deleteAppFromFocusMode(appInFocusMode: AppInFocusMode)

    suspend fun updateAppInFocusMode(appInFocusMode: AppInFocusMode)
}