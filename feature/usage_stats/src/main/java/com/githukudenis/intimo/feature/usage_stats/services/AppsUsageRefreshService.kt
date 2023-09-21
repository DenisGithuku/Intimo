package com.githukudenis.intimo.feature.usage_stats.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.githukudenis.intimo.core.data.repository.AppsUsageRepository
import com.githukudenis.intimo.core.data.repository.UsageStatsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import javax.inject.Inject

@AndroidEntryPoint
class AppsUsageRefreshService : Service() {

    @Inject
    lateinit var usageStatsRepository: UsageStatsRepository

    @Inject
    lateinit var appsUsageRepository: AppsUsageRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("usage service", "app usage service started")
//        scope.launch {
//            val appsInFocusMode = appsUsageRepository.appsInFocusMode
//            val aggregatedUsageStats = usageStatsRepository.queryAndAggregateUsageStats(
//                LocalDate.now()
//            )
//                .map { it.appUsageList }
//
//            combine(appsInFocusMode, aggregatedUsageStats) { appsInFocus, aggregatedApps ->
//                aggregatedApps.forEach { appData ->
//                    if (appData.packageName in appsInFocus.map { it.packageName }) {
//                        appsInFocus.find { it.packageName == appData.packageName }
//                            ?.let { focusApp ->
//                                    appsUsageRepository.updateAppInFocusMode(
//                                        focusApp.copy(limitReached = appData.usageDuration > focusApp.limitDuration)
//                                    )
//                            }
//                    }
//                }
//            }.stateIn(scope)
//        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}