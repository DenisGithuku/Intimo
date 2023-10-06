package com.githukudenis.intimo.feature.summary.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.intimo.core.data.repository.HabitsRepository
import com.githukudenis.intimo.core.data.repository.UsageStatsRepository
import com.githukudenis.intimo.core.model.DurationType
import com.githukudenis.intimo.core.model.HabitData
import com.githukudenis.intimo.core.ui.components.Date
import com.githukudenis.intimo.core.util.UserMessage
import com.githukudenis.intimo.feature.summary.ui.components.HabitPerformance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

    val customHabitState: MutableStateFlow<CustomHabitState> = MutableStateFlow(CustomHabitState())

    private var usageStatsState: MutableStateFlow<UsageStatsState> =
        MutableStateFlow(UsageStatsState.Loading)

    private var permissionsState = MutableStateFlow(PermissionState())

    private var queryDetails = MutableStateFlow(QueryTime(date = LocalDate.now()))

    private var userMessageList = MutableStateFlow(emptyList<UserMessage>())


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

        /* map day by completed habits as a value between 0f and 1f */
        val historyState = completed.associate { dayAndHabits ->
            Pair(
                Date(
                    date = Instant.fromEpochMilliseconds(dayAndHabits.day.dayId).toLocalDateTime(
                        TimeZone.currentSystemDefault()
                    ).date.toJavaLocalDate()
                ), (dayAndHabits.habits.size.toFloat() / selected.size) * 1f
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

        HabitsState.Success(runningHabitState = runningHabitState,
            habitDataList = selected.filterNot { habit ->
                habit.habitId in completed.filter { it.day.dayId == today }.flatMap { it.habits }
                    .map { it.habitId }
            }.sortedBy { habit -> habit.startTime }.map { habit ->
                HabitUiModel(
                    completed = Pair(completed.first { it.day.dayId == today }.day.dayId,
                        habit.habitId in completed.filter { it.day.dayId == today }
                            .flatMap { it.habits }.map { it.habitId }),
                    habitId = habit.habitId,
                    habitIcon = habit.habitIcon,
                    habitName = habit.habitName,
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

    var uiState: StateFlow<SummaryUiState> = combine(
        usageStatsState,
        habitHistoryState,
        userMessageList,
        permissionsState,
        customHabitState
    ) { usageStats, habitsState, userMessageList, permissionState, customHabitState ->

        SummaryUiState(
            isLoading = false,
            usageStatsState = usageStats,
            habitsState = habitsState,
            userMessageList = userMessageList,
            permissionsState = permissionState,
            customHabitState = customHabitState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = SummaryUiState(isLoading = true)
    )

    init {
        getUsageStats()
    }

    fun onEvent(event: SummaryUiEvent) {
        when (event) {
            SummaryUiEvent.Refresh -> {
                getUsageStats()
            }

            is SummaryUiEvent.PermissionChange -> {
                permissionsState.update { event.permissionState }
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

            is SummaryUiEvent.ChangeHabitDays -> {
                customHabitState.update { state ->
                    state.copy(days = event.habitDays)
                }
            }

            is SummaryUiEvent.ChangeHabitDuration -> {
                customHabitState.update { state ->
                    state.copy(
                        habitDuration = event.duration,
                        habitDurationType = if (event.duration >= 1000L * 60 * 60) DurationType.HOUR else DurationType.MINUTE
                    )
                }
            }

            is SummaryUiEvent.ChangeHabitFrequency -> {
                customHabitState.update { state ->
                    state.copy(habitFrequency = event.habitFrequency)
                }
            }

            is SummaryUiEvent.ChangeHabitName -> {
                customHabitState.update { state ->
                    state.copy(habitName = event.habitName)
                }
            }

            is SummaryUiEvent.ChangeHabitStartTime -> {
                customHabitState.update { state ->
                    state.copy(startTime = event.startTime)
                }
            }

            is SummaryUiEvent.ChangeRemindTime -> {
                customHabitState.update { state ->
                    state.copy(remindTime = event.remindTime)
                }
            }

            is SummaryUiEvent.ChangeHabitIcon -> {
                customHabitState.update { state ->
                    state.copy(habitIcon = event.icon)
                }
            }

            SummaryUiEvent.SaveHabit -> {
                saveHabit()
            }
        }
    }

    private fun getUsageStats() {
        viewModelScope.launch {

            val notificationsByDay = usageStatsRepository.dayAndNotificationList.first()

            val usageStats = usageStatsRepository.queryAndAggregateUsageStats(
                startDate = queryDetails.value.date ?: LocalDate.now(),
                endDate = queryDetails.value.date ?: LocalDate.now()
            )

            val stats = if (usageStats.appUsageList.isEmpty()) {
                UsageStatsState.Empty
            } else {
                UsageStatsState.Loaded(usageStats = usageStats.appUsageList,
                    unlockCount = usageStats.unlockCount,
                    notificationCount = notificationsByDay.filter { it.day.dayId == today }
                        .flatMap { it.notifications }.size
                )
            }

            usageStatsState.update {
                stats
            }
        }
    }

    //    private fun fetchHabitHistory(dayId: Long) {
//        habitUiModelList = habitUiModelList.
//    }
    private fun saveHabit() {
        viewModelScope.launch {
            val (habitName, habitIcon, startTime, habitDuration, durationType, habitFrequency, habitDays, remindTime) = customHabitState.value
            val habit = HabitData(
                habitIcon = habitIcon,
                habitName = habitName,
                startTime = startTime,
                duration = habitDuration,
                durationType = durationType,
                habitFrequency = habitFrequency,
                habitDays = habitDays,
                remindTime = remindTime
            )
            habitsRepository.insertHabit(habit)
            val messages = userMessageList.value
            messages.toMutableList().add(
                UserMessage(
                    message = "New habit added successfully"
                )
            )
            userMessageList.update { messages }
        }
    }
}


data class QueryTime(
    val date: LocalDate?
)