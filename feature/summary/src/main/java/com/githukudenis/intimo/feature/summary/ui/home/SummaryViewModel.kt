package com.githukudenis.intimo.feature.summary.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.intimo.core.data.repository.HabitsRepository
import com.githukudenis.intimo.core.data.repository.UsageStatsRepository
import com.githukudenis.intimo.core.ui.components.Date
import com.githukudenis.intimo.core.util.UserMessage
import com.githukudenis.intimo.feature.summary.ui.components.HabitPerformance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
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

    private val usageStatsState: MutableStateFlow<UsageStatsState>
        get() = MutableStateFlow(UsageStatsState.Loading)

    private var queryDetails =
        MutableStateFlow(QueryTime(date = LocalDate.now()))

    private var userMessageList = MutableStateFlow(emptyList<UserMessage>())


    private var usageStatsJob: Job? = null

    private val habitHistoryState = combine(
        habitsRepository.selectedHabitList,
        habitsRepository.completedHabitList,
        habitsRepository.runningHabits
    ) { selected, completed, running ->

        val runningHabitState = if (running.isNotEmpty()) {
            val habit = running.first()
            RunningHabitState(
                habitId = habit.habitId,
                isRunning = habit.isRunning,
                remainingTime = habit.remainingTime
            )
        } else {
            RunningHabitState()
        }

        val historyState = completed.associate { dayAndHabits ->
            Pair(
                Date(
                    date = Instant.fromEpochMilliseconds(dayAndHabits.day.dayId).toLocalDateTime(
                        TimeZone.currentSystemDefault()
                    ).date.toJavaLocalDate()
                ),
                (dayAndHabits.habits.size.toFloat() / selected.size) * 1f
            )
        }

        val habitPerformance = when {
            historyState.getValue(
                Date(
                    queryDetails.value.date ?: LocalDate.now()
                )
            ) >= 0.75f -> HabitPerformance.EXCELLENT

            historyState.getValue(
                Date(
                    queryDetails.value.date ?: LocalDate.now()
                )
            ) >= 0.45f -> HabitPerformance.GOOD

            else -> HabitPerformance.POOR
        }

        HabitsState.Success(
            runningHabitState = runningHabitState,
            habitDataList = selected
                .filterNot { habit ->
                    habit.habitId in completed.filter { it.day.dayId == today }
                        .flatMap { it.habits }.map { it.habitId }
                }
                .sortedBy { habit -> habit.startTime }
                .map { habit ->
                    HabitUiModel(
                        completed = Pair(
                            completed.first { it.day.dayId == today }.day.dayId,
                            habit.habitId in completed.filter { it.day.dayId == today }
                                .flatMap { it.habits }.map { it.habitId }),
                        habitId = habit.habitId,
                        habitIcon = habit.habitIcon,
                        habitType = habit.habitType,
                        startTime = habit.startTime,
                        duration = habit.duration,
                        durationType = habit.durationType,
                        remainingTime = runningHabitState.remainingTime
                    )
                },
            habitHistoryStateList = historyState,
            habitPerformance = habitPerformance
        )
    }

    private val usageStats = combine(queryDetails, usageStatsRepository.dayAndNotificationList)
        { queryDetails, notificationsByDay ->
        val usageStats = usageStatsRepository.queryAndAggregateUsageStats(
            startDate = queryDetails.date ?: LocalDate.now(),
            endDate = queryDetails.date ?: LocalDate.now()
        )

        if (usageStats.appUsageList.isEmpty()) {
            UsageStatsState.Empty
        } else {
            UsageStatsState.Loaded(
                usageStats = usageStats.appUsageList,
                unlockCount = usageStats.unlockCount,
                notificationCount = notificationsByDay.filter { it.day.dayId == today }.flatMap { it.notifications }.size
            )
        }
    }


    var uiState: StateFlow<SummaryUiState> = combine(
        usageStats,
        habitHistoryState,
        userMessageList,
    ) { usageStats, habitsState, userMessageList ->

        SummaryUiState(
            isLoading = false,
            usageStatsState = usageStats,
            habitsState = habitsState,
            userMessageList = userMessageList,
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

data class QueryTime(
    val date: LocalDate?
)