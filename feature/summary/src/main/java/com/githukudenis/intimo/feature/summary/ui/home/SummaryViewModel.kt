package com.githukudenis.intimo.feature.summary.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.intimo.core.data.repository.HabitsRepository
import com.githukudenis.intimo.core.data.repository.UsageStatsRepository
import com.githukudenis.intimo.core.model.DataUsageStats
import com.githukudenis.intimo.core.ui.components.Date
import com.githukudenis.intimo.core.util.UserMessage
import com.githukudenis.intimo.feature.summary.ui.components.HabitPerformance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDate
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val usageStatsRepository: UsageStatsRepository,
    private val habitsRepository: HabitsRepository,
) : ViewModel() {

    private val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private var queryDetails =
        MutableStateFlow(QueryTime(date = LocalDate.now()))

    private val usageStats = combine(
        usageStatsRepository.queryAndAggregateUsageStats(
            date = queryDetails.value.date ?: LocalDate.now()
        ),
        usageStatsRepository.dayAndNotificationList,
    ) { usageStats, dayAndNotifications ->
        Pair(usageStats, dayAndNotifications)
    }

    private val habitHistoryState = combine(
        habitsRepository.selectedHabitList,
        habitsRepository.completedHabitList
    ) { selected, completed ->
        completed.associate { dayAndHabits ->
            Pair(
                Date(
                    date = Instant.fromEpochMilliseconds(dayAndHabits.day.dayId).toLocalDateTime(
                        TimeZone.currentSystemDefault()
                    ).date.toJavaLocalDate()
                ),
                (dayAndHabits.habits.size.toFloat() / selected.size) * 1f
            )
        }
    }

    private var habitUiModelList = combine(
        habitsRepository.selectedHabitList,
        habitsRepository.completedHabitList,
        habitsRepository.runningHabits
    ) { active, completed, running ->
        active
            .filterNot {  activeHabit ->
                activeHabit in completed.filter { it.day.dayId == today }.flatMap { it.habits }
            }
            .map { habitData ->

            val remTime = if (running.isNotEmpty()) {
                running.find { it.habitId == habitData.habitId }?.remainingTime ?: 0L
            } else {
                0L
            }

            HabitUiModel(
                completed = Pair(
                    completed.first { it.day.dayId == today }.day.dayId,
                    habitData.habitId in completed.filter { it.day.dayId == today }
                        .flatMap { it.habits }.map { it.habitId }),
                habitId = habitData.habitId,
                habitIcon = habitData.habitIcon,
                habitType = habitData.habitType,
                startTime = habitData.startTime,
                duration = habitData.duration,
                durationType = habitData.durationType,
                remainingTime = remTime
            )
        }
    }

    private var userMessageList = MutableStateFlow(emptyList<UserMessage>())

    var uiState: StateFlow<SummaryUiState> = combine(
        usageStats,
        habitUiModelList,
        userMessageList,
        habitsRepository.runningHabits,
        habitHistoryState
    ) { usageStats, habitList, userMessageList, running, history ->
        val summaryData = SummaryData(
            usageStats.first,
            usageStats.first.unlockCount
        )
        val runningHabitState = if (running.isNotEmpty()) {
            running.first().run {
                RunningHabitState(
                    habitId, isRunning, remainingTime
                )
            }
        } else {
            RunningHabitState()
        }

        SummaryUiState(
            isLoading = false,
            summaryData = summaryData,
            notificationCount = usageStats.second.filter { it.day.dayId == today }
                .flatMap { it.notifications }.size,
            runningHabitState = runningHabitState,
            habitDataList = habitList.sortedBy { it.startTime },
            habitPerformance = when {
                history.getValue(
                    Date(
                        queryDetails.value.date ?: LocalDate.now()
                    )
                ) >= 0.75f -> HabitPerformance.EXCELLENT
                history.getValue(
                    Date(
                        queryDetails.value.date ?: LocalDate.now()
                    )
                ) >= 0.45f -> HabitPerformance.GOOD
                else -> HabitPerformance.POOR
            },
            userMessageList = userMessageList,
            habitHistoryStateList = history,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = SummaryUiState(isLoading = true)
        )

    fun onEvent(event: SummaryUiEvent) {
        when (event) {
            SummaryUiEvent.Refresh -> {

            }

            is SummaryUiEvent.ShowMessage -> {
                val messageList = userMessageList.value.toMutableList()
                messageList.add(event.error)
                userMessageList.update { messageList }
            }

            is SummaryUiEvent.DismissMessage -> {
                val messageList = userMessageList.value.filterNot { it.id == event.messageId }
                userMessageList.update { messageList }
            }

            is SummaryUiEvent.SelectDayOnHistory -> {
//                fetchHabitHistory(
//                    event.date.date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
//                )
            }
        }
    }

//    private fun fetchHabitHistory(dayId: Long) {
//        habitUiModelList = habitUiModelList.
//    }
}

data class SummaryData(
    val usageStats: DataUsageStats,
    val unlockCount: Int,
)

data class QueryTime(
    val date: LocalDate?
)