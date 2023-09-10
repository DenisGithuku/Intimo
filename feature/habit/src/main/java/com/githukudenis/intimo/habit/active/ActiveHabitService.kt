package com.githukudenis.intimo.habit.active

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import com.githukudenis.data.repository.HabitsRepository
import com.githukudenis.intimo.core.designsystem.R
import com.githukudenis.intimo.habit.components.formatCountdownTime
import com.githukudenis.model.HabitType
import com.githukudenis.model.RunningHabit
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

const val pendingIntentId = 102

@AndroidEntryPoint
class ActiveHabitService : Service() {

    private lateinit var notificationManager: NotificationManager

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Inject
    lateinit var habitsRepository: HabitsRepository

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
           intent.extras?.let innerLet@ { extras ->

                val notificationData = NotificationData(
                    title = extras.getString("title"),
                    content = extras.getString("content"),
                    habit = RunningHabit(
                        habitId = extras.getLong("habitId"),
                        totalTime = extras.getLong("duration"),
                        isRunning = true,
                        remainingTime = extras.getLong("duration"),
                        habitType = HabitType.valueOf(extras.getString("habitType") ?: return@innerLet )
                    )
                )

                when (action) {
                    NotificationAction.START.toString() -> start(notificationData)
                    NotificationAction.STOP.toString() -> {
                        notificationManager.cancel(notificationData.habit.habitId.toInt())
                        stop()
                    }
                }
            }
        }
        return START_STICKY
    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

    private fun start(notificationData: NotificationData) {
        try {
            val notification = createNotification(notificationData)
            notificationManager.notify(notificationData.habit.habitId.toInt(), notification.build())

            scope.launch {
                if (notificationData.habit.remainingTime <= 0L) {
                    stop()
                    return@launch
                }
                var init = notificationData.habit.remainingTime
                while (init >= 0L) {
                    habitsRepository.updateRunningHabit(notificationData.habit.copy(remainingTime = init))
                    val updatedNotification = notification.setContentText(
                        "Remaining time: ${formatCountdownTime(init)}"
                    )

                    notificationManager.notify(notificationData.habit.habitId.toInt(), updatedNotification.build())
                    init -= 1000L
                    delay(1000L)
                }

            }
            startForeground(notificationData.habit.habitId.toInt(), notification.build())
        } catch (e: Exception) {
            Log.e("habit update error", e.localizedMessage ?: "Could not update habit")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }


    private fun createNotification(
        notificationData: NotificationData
    ): NotificationCompat.Builder {

        val intent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(
                Intent(
                    Intent.ACTION_VIEW,
                    "intimo://active_habit/${notificationData.habit.habitId}".toUri()
                )
            )
            getPendingIntent(pendingIntentId, PendingIntent.FLAG_UPDATE_CURRENT)
        }


        return NotificationCompat.Builder(this, "intimo_notifications")
            .setContentTitle(notificationData.title)
            .setContentText(notificationData.content)
            .setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
            .setSmallIcon(R.drawable.intimologo)
            .setContentIntent(intent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)

    }


    enum class NotificationAction {
        START,
        STOP,
    }

    data class NotificationData(
        val title: String? = null,
        val content: String? = null,
        val habit: RunningHabit
    )
}