package com.githukudenis.summary.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.data.repository.HabitsRepository
import com.githukudenis.data.repository.UsageStatsRepository
import com.githukudenis.model.DataUsageStats
import com.githukudenis.summary.ui.UserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    usageStatsRepository: UsageStatsRepository,
    habitsRepository: HabitsRepository,
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

    private var habitUiModelList = combine(
        habitsRepository.selectedHabitList,
        habitsRepository.completedHabitList,
        habitsRepository.runningHabits
    ) { active, completed, running ->
        active.map { habitData ->

            val remTime = if (running.isNotEmpty()) {
                running.find { it.habitId == habitData.habitId }?.remainingTime ?: 0L
            } else {
                0L
            }

            HabitUiModel(
                completed = habitData.habitId in completed.filter { it.day.dayId == today }
                    .flatMap { it.habits }.map { it.habitId },
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
        habitsRepository.runningHabits
    ) { usageStats, habitList, userMessageList, running ->
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
            summaryData = summaryData,
            notificationCount = usageStats.second.filter { it.day.dayId == today }
                .flatMap { it.notifications }.size,
            habitDataList = habitList.sortedBy { it.startTime },
            userMessageList = userMessageList,
            isLoading = false,
            runningHabitState = runningHabitState
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
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
        }
    }

}

data class SummaryData(
    val usageStats: DataUsageStats,
    val unlockCount: Int,
)

data class QueryTime(
    val date: LocalDate?
)