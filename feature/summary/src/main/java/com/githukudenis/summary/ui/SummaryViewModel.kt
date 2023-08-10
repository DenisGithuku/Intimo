package com.githukudenis.summary.ui

import android.app.usage.UsageStatsManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.data.repository.HabitsRepository
import com.githukudenis.data.repository.IntimoUsageStatsRepository
import com.githukudenis.data.repository.IntimoUserDataRepository
import com.githukudenis.model.DataUsageStats
import com.githukudenis.model.HabitData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val intimoUserDataRepository: IntimoUserDataRepository,
    private val intimoUsageStatsRepository: IntimoUsageStatsRepository,
    private val habitsRepository: HabitsRepository
) : ViewModel() {

    var uiState: MutableStateFlow<SummaryUiState> = MutableStateFlow(SummaryUiState())
        private set

    var queryDetails = MutableStateFlow(QueryTime())
        private set

    init {
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.set(Calendar.HOUR, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        queryDetails.update {
            it.copy(
                beginTime = startTime,
                endTime = endTime,
                interval = UsageStatsManager.INTERVAL_BEST
            )
        }
        getUsageStats()
        getHabitData()
    }

    fun onEvent(event: SummaryUiEvent) {
        when (event) {
            SummaryUiEvent.Refresh -> {
                getUsageStats()
            }

            is SummaryUiEvent.ShowError -> {

                val userErrorList = mutableListOf<UserError>()
                userErrorList.add(event.error)
                val newCurrentState = uiState.value.copy(userErrorList = userErrorList)
                uiState.update {
                    newCurrentState
                }
            }

            is SummaryUiEvent.CheckHabit -> {
                checkHabit(event.habitId)
            }
        }
    }

    private fun getUsageStats() {
        viewModelScope.launch {
            val usageStats = intimoUsageStatsRepository.queryAndAggregateUsageStats(
                beginTime = queryDetails.value.beginTime,
                endTime = queryDetails.value.endTime
            )
            val userData = intimoUserDataRepository.userData
            combine(usageStats, userData) { stats, data ->
                uiState.update { currentState ->
                    currentState.copy(
                        summaryData = SummaryData(
                            usageStats = stats,
                            unlockCount = stats.unlockCount
                        ),
                        notificationCount = data.notificationCount
                    )
                }
            }.collect()
        }
    }

    private fun checkHabit(habitId: Int) {
        viewModelScope.launch {
            var habit = uiState.value.habitDataList.find { habit -> habit.habitDataId == habitId } ?: return@launch
            habit = habit.copy(habitPoints = 1)
            habitsRepository.updateHabit(habit)
            getHabitData()
        }
    }

    private fun getHabitData() {
        viewModelScope.launch {
            habitsRepository.getHabitList()
                .onEach { habitDataList ->
                    uiState.update { currentState ->
                        currentState.copy(
                            habitDataList = habitDataList.flatMap { it.habitData }
                        )
                    }
                }
                .collect()
        }
    }

}

data class SummaryUiState(
    val isLoading: Boolean = false,
    val summaryData: SummaryData? = null,
    val notificationCount: Long = 0L,
    val habitDataList: List<HabitData> = emptyList(),
    val userErrorList: List<UserError> = emptyList()
)

data class SummaryData(
    val usageStats: DataUsageStats,
    val unlockCount: Int,
)

data class QueryTime(
    val beginTime: Long = 0L,
    val endTime: Long = 0L,
    val interval: Int = 0
)