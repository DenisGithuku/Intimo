package com.githukudenis.summary.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.data.repository.HabitsRepository
import com.githukudenis.data.repository.UsageStatsRepository
import com.githukudenis.data.repository.UserDataRepository
import com.githukudenis.model.DataUsageStats
import com.githukudenis.model.HabitData
import com.githukudenis.summary.ui.UserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val usageStatsRepository: UsageStatsRepository,
    private val habitsRepository: HabitsRepository,
) : ViewModel() {

    private val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    var habitInEditMode = MutableStateFlow(HabitInEditModeState())
        private set

    private var queryDetails =
        MutableStateFlow(QueryTime(date = LocalDate.now()))

    private var habitUiModelList = combine(
        habitsRepository.selectedHabitList,
        habitsRepository.completedHabitList,
    ) { active, completed ->
        active.map { habitData ->
            HabitUiModel(
                completed = habitData.habitId in completed.filter { it.day.dayId == today }
                    .flatMap { it.habits }.map { it.habitId },
                habitId = habitData.habitId,
                habitIcon = habitData.habitIcon,
                habitType = habitData.habitType,
                startTime = habitData.startTime,
                duration = habitData.duration,
                durationType = habitData.durationType
            )
        }
    }

    private var userMessageList = MutableStateFlow(emptyList<UserMessage>())

    var uiState: StateFlow<SummaryUiState> = combine(
        usageStatsRepository.queryAndAggregateUsageStats(
            date = queryDetails.value.date ?: LocalDate.now()
        ),
        userDataRepository.userData,
        habitInEditMode,
        habitUiModelList,
        userMessageList
    ) { usageStats, userData, habitInEditMode, habitList, userMessageList ->
        val summaryData = SummaryData(
            usageStats,
            usageStats.unlockCount
        )
        SummaryUiState(
            summaryData = summaryData,
            notificationCount = userData.notificationCount,
            habitDataList = habitList,
            userMessageList = userMessageList,
            habitInEditModeState = habitInEditMode,
            isLoading = false
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

            is SummaryUiEvent.StartHabit -> {
                startHabit(event.habitId)
            }

            SummaryUiEvent.UpdateHabit -> {
                if (uiState.value.habitInEditModeState.habitModel == null) {
                    return
                }
                uiState.value.habitInEditModeState.habitModel?.let {
                    updateHabit(
                        HabitData(
                            it.habitId,
                            it.habitIcon,
                            it.habitType,
                            it.startTime,
                            it.duration,
                            it.durationType
                        )
                    )
                }
                clearHabitInEditMode()
            }

            is SummaryUiEvent.ShowMessage -> {
                val messageList = userMessageList.value.toMutableList()
                messageList.add(event.error)
                userMessageList.update { messageList }
            }

            is SummaryUiEvent.DismissMessage -> {
                val messageList = userMessageList.value.filter { it.id == event.messageId }
                userMessageList.update { messageList }
            }

            is SummaryUiEvent.EditHabit -> {
                val habitInEditing =
                    uiState.value.habitDataList.find { it.habitId == event.habitId } ?: return
                habitInEditMode.update {
                    it.copy(habitModel = habitInEditing)
                }
            }
        }
    }

    private fun startHabit(habitId: Long) {
        viewModelScope.launch {
            val habitData = habitsRepository.selectedHabitList.first().find {
                it.habitId == habitId
            }

            Log.d("active habit", habitData.toString())
            habitData?.let {
                userDataRepository.updateHabitTime(it.duration)
            }
        }
    }

    private fun updateHabit(habitData: HabitData) {
        viewModelScope.launch {
            habitsRepository.updateHabit(habitData)
        }
    }

    private fun clearHabitInEditMode() {
        habitInEditMode.update {
            HabitInEditModeState(null)
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