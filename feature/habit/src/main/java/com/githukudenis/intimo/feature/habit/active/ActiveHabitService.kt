package com.githukudenis.intimo.feature.habit.active

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
import com.githukudenis.intimo.core.data.repository.HabitsRepository
import com.githukudenis.intimo.core.designsystem.R
import com.githukudenis.intimo.feature.habit.components.formatCountdownTime
import com.githukudenis.intimo.core.model.HabitType
import com.githukudenis.intimo.core.model.RunningHabit
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

    private var notificationId: Int? = null

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
                        habitName = extras.getString("habitName") ?: return@innerLet
                    )
                )

                when (action) {
                    NotificationAction.START.toString() -> start(notificationData)
                    NotificationAction.STOP.toString() -> stop(notificationData.habit.habitId.toInt())
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun stop(notificationId: Int) {
        notificationManager.cancel(notificationId)
        stopForeground(true)
        stopSelf()
    }

    private fun start(notificationData: NotificationData) {
        try {
            val notification = createNotification(notificationData)
            notificationManager.notify(notificationData.habit.habitId.toInt(), notification.build())

            scope.launch {
                var init = notificationData.habit.remainingTime
                while (init >= 0L) {
                    habitsRepository.updateRunningHabit(notificationData.habit.copy(remainingTime = init))
                    val updatedNotification = notification.setContentText(
                        "Remaining time: ${formatCountdownTime(init)}"
                    )
                    notificationId = notificationData.habit.habitId.toInt()
                    notificationManager.notify(notificationData.habit.habitId.toInt(), updatedNotification.build())
                    init -= 1000L
                    if (init < 0) {
                        stop(notificationData.habit.habitId.toInt())
                        return@launch
                    }
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
        notificationId?.let { notifId -> notificationManager.cancel(notifId) }
    }


    private fun createNotification(
        notificationData: NotificationData
    ): NotificationCompat.Builder {

        val ACTION_OPEN_MAIN_ACTIVITY = "com.githukudenis.intimo.ACTION_OPEN_MAIN_ACTIVITY"
        val intent = Intent(ACTION_OPEN_MAIN_ACTIVITY).apply {
            setPackage("com.githukudenis.intimo")
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val mainIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)


        return NotificationCompat.Builder(this, "active_habit_notifs")
            .setContentTitle(notificationData.title)
            .setContentText(notificationData.content)
            .setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
            .setSmallIcon(R.drawable.intimologo)
            .setContentIntent(mainIntent)
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