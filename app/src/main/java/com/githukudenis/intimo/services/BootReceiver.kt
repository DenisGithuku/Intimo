package com.githukudenis.intimo.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.githukudenis.intimo.core.data.util.DataSyncManager
import com.githukudenis.intimo.feature.usage_stats.AppLaunchService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED ||
            intent?.action == Intent.ACTION_REBOOT) {
            val dataSyncManager = DataSyncManager()
            if (context != null) {
                dataSyncManager.setupRefreshAlarm(context)
            }

            context?.let {
                val intent = Intent(context, AppLaunchService::class.java)
                context.startService(intent)
            }
        }
    }
}