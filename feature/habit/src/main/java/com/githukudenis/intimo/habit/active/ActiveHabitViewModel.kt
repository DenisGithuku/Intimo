package com.githukudenis.intimo.habit.active

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.data.repository.HabitsRepository
import com.githukudenis.model.RunningHabit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HabitActiveViewModel @Inject constructor(
    private val habitsRepository: HabitsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val timerState = MutableStateFlow(TimerState())

    private val habitDataDeferred = viewModelScope.async {
        habitsRepository.getHabitById(requireNotNull(savedStateHandle.get<Long>("habitId")))
    }

    val uiState: StateFlow<ActiveHabitUiState> = combine(
        habitsRepository.runningHabits,
        timerState
    ) { runningHabits, timerState ->

        val habitData = habitDataDeferred.await()

        val (currentTime, isRunning) =
            if (timerState.currentTime == 0L && runningHabits.isNullOrEmpty()) Pair(
                habitData.duration,
                false
            )
            else {
                val currentRunningHabit = runningHabits.first {
                    it.habitId == requireNotNull(savedStateHandle.get<Long>("habitId"))
                }
                Pair(currentRunningHabit.remainingTime, currentRunningHabit.isRunning)
            }

        ActiveHabitUiState(
            habitId = savedStateHandle.get<Long>("habitId"),
            habitData = habitData,
            timerState = timerState.copy(
                totalTime = habitData.duration,
                /* In init runningHabit is null. On start data becomes available */
                currentTime = currentTime,
                isRunning = isRunning
            )
        )
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            ActiveHabitUiState()
        )

    fun onStartHabit() {
        viewModelScope.launch {
            val habitData = habitsRepository.selectedHabitList.first().find {
                it.habitId == uiState.value.habitId
            }
            habitData?.let {
                habitsRepository.insertRunningHabit(
                    RunningHabit(
                        habitId = habitData.habitId,
                        isRunning = true,
                        habitType = habitData.habitType,
                        totalTime = habitData.duration,
                        remainingTime = habitData.duration
                    )
                )
            }
        }
    }

    fun onCompleteHabit() {
        completeHabit()
        deleteRunningHabit()
    }

    fun onRestartHabit() {
        onStartHabit()
    }

    private fun deleteRunningHabit() {
        viewModelScope.launch {
            uiState.value.habitData?.let { habit ->
                habitsRepository.deleteRunningHabit(
                    RunningHabit(
                        habitId = habit.habitId,
                        isRunning = false,
                        habitType = habit.habitType,
                        totalTime = habit.duration,
                        remainingTime = habit.duration
                    )
                )
            }
        }
    }

    private fun completeHabit() {
        viewModelScope.launch {
            uiState.value.habitData?.let { habit ->
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

    }

    fun onCancelHabit() {
        deleteRunningHabit()
    }

}