package com.githukudenis.intimo.core.data.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.githukudenis.intimo.core.data.di.IntimoCoroutineDispatcher
import com.githukudenis.intimo.core.data.repository.HabitsRepository
import com.githukudenis.intimo.core.model.Day
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class DataSyncManager : BroadcastReceiver() {

    @Inject
    lateinit var intimoCoroutineDispatcher: IntimoCoroutineDispatcher

    @Inject
    lateinit var habitsRepository: HabitsRepository

    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    private val dayId = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    override fun onReceive(p0: Context?, p1: Intent?) {
        refreshHabitData()
    }

    private fun refreshHabitData() {
        scope.launch {
            habitsRepository.insertDay(
                Day(dayId = dayId)
            )
        }
    }

    fun setupRefreshAlarm(context: Context) {
        val intent = Intent(context, DataSyncManager::class.java)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC,
            dayId,
            AlarmManager.INTERVAL_DAY,
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE)
        )
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val dataSyncManager = DataSyncManager()
            if (context != null) {
                dataSyncManager.setupRefreshAlarm(context)
            }
        }
    }
}