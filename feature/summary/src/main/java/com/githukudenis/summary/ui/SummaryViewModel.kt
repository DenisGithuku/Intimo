package com.githukudenis.summary.ui

import android.app.usage.UsageStatsManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.data.di.IntimoCoroutineDispatcher
import com.githukudenis.data.repository.HabitsRepository
import com.githukudenis.data.repository.IntimoUsageStatsRepository
import com.githukudenis.data.repository.IntimoUserDataRepository
import com.githukudenis.model.DataUsageStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val intimoUserDataRepository: IntimoUserDataRepository,
    private val intimoUsageStatsRepository: IntimoUsageStatsRepository,
    private val habitsRepository: HabitsRepository,
    private val intimoCoroutineDispatcher: IntimoCoroutineDispatcher
) : ViewModel() {

    val calendar = Calendar.getInstance()
    val endTime = calendar.timeInMillis
    val beginTime = calendar.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private var queryDetails =
        MutableStateFlow(QueryTime(beginTime, endTime, UsageStatsManager.INTERVAL_DAILY))


    var uiState: StateFlow<SummaryUiState> = combine(
        intimoUsageStatsRepository.queryAndAggregateUsageStats(
            beginTime = queryDetails.value.beginTime,
            endTime = queryDetails.value.endTime
        ),
        intimoUserDataRepository.userData,
        habitsRepository.activeHabitList,
        habitsRepository.completedHabitList
    ) { usageStats, userData, activeHabitList, completedHabitList ->
        val summaryData = SummaryData(
            usageStats,
            usageStats.unlockCount
        )
        SummaryUiState(
            summaryData = summaryData,
            notificationCount = userData.notificationCount,
            habitDataList = activeHabitList.map {
                val completed = it.habitId in completedHabitList.flatMap { it.habits }.map { it.habitId }
                Log.d("completed", completedHabitList.toString())
                Log.d("completed ac", activeHabitList.toString())
                it.toHabitUiModel(completed)
            },
            completedHabits = completedHabitList.flatMap { it.habits }
        )
    }
        .onStart {
            emit(SummaryUiState(isLoading = true))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SummaryUiState()
        )

    fun onEvent(event: SummaryUiEvent) {
        when (event) {
            SummaryUiEvent.Refresh -> {

            }

            is SummaryUiEvent.ShowError -> {

//                val userErrorList = mutableListOf<UserError>()
//                userErrorList.add(event.error)
//                val newCurrentState = uiState.value.copy(userErrorList = userErrorList)
//                uiState.value = uiState.value.copy()
            }

            is SummaryUiEvent.CheckHabit -> {
                completeHabit(event.habitId)
            }
        }
    }

    private fun completeHabit(habitId: Long) {
        viewModelScope.launch {
            val habit =
                uiState.value.habitDataList.find { habit -> habit.habitId == habitId }
                    ?: return@launch
            if (habit.habitId in uiState.value.completedHabits.map { it.habitId }) {
                return@launch
            }
            val dayId = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            habitsRepository.completeHabit(
                dayId = dayId,
                habitId = habit.habitId
            )
        }
    }

//    private fun getHabitData() {
//        viewModelScope.launch {
//            val completedHabits = habitsRepository.completedHabitList
//            val activeHabits = habitsRepository.activeHabitList
//
//            combine(completedHabits, activeHabits) { completed, active ->
//                uiState.update { currentState ->
//                    currentState.copy(
//                        habitDataList = active.map { habitData ->
//                            Log.d("completed", completed.toString())
//                            HabitUiModel(
//                                completed = habitData.habitId in completed.flatMap { it.habits }
//                                    .map { it.habitId },
//                                habitId = habitData.habitId,
//                                habitIcon = habitData.habitIcon,
//                                habitType = habitData.habitType,
//                                startTime = habitData.startTime,
//                                duration = habitData.duration
//                            )
//                        },
//                        days = completed.map { it.day },
//                        completedHabits = completed.flatMap { it.habits }
//                    )
//                }
//            }
//                .collect()
//        }
//    }

}

data class SummaryData(
    val usageStats: DataUsageStats,
    val unlockCount: Int,
)

data class QueryTime(
    val beginTime: Long = 0L,
    val endTime: Long = 0L,
    val interval: Int = 0
)