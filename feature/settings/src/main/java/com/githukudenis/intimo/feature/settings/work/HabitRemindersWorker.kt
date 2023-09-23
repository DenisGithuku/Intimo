package com.githukudenis.intimo.feature.settings.work

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.githukudenis.intimo.core.data.repository.HabitsRepository
import com.githukudenis.intimo.feature.settings.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDate
import javax.inject.Inject

const val habitRemindersNotificationChannelId = "habit_reminders_notifs"
const val habitRemindersNotificationId = 234234

@HiltWorker
class HabitRemindersWorker @AssistedInject constructor(
    @Assisted private val habitsRepository: HabitsRepository,
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val today = LocalDate.now()

    private val notificationManager: NotificationManager by lazy {
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override suspend fun doWork(): Result {
        return try {
            val habitDoneWithinLastTwoDays = habitsRepository.completedHabitList
                .first()
                .any {
                    val habitDay = Instant.fromEpochMilliseconds(it.day.dayId)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .date
                        .toJavaLocalDate()

                    val dayBeforeYesterday = today.minusDays(2)
                    (dayBeforeYesterday..today).contains(habitDay)
                }
            if (!habitDoneWithinLastTwoDays) {
                notificationManager.notify(
                    habitRemindersNotificationId,
                    NotificationUtils.alertNotification(
                        text = applicationContext.getString(R.string.habits_not_completed_title),
                        title = applicationContext.getString(R.string.habits_not_completed_text),
                        context = applicationContext
                    )
                )
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("Intimo", e.message, e)
            Result.failure()
        }
    }
}

class NotificationUtils {
    companion object {
        fun alertNotification(
            title: String,
            text: String,
            context: Context
        ): Notification {
            return NotificationCompat.Builder(context, habitRemindersNotificationChannelId)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(
                    com.githukudenis.intimo.core.designsystem.R.drawable.intimologo,
                )
                .build()
        }
    }
}