package com.githukudenis.intimo.feature.habit.active

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.intimo.core.data.repository.HabitsRepository
import com.githukudenis.intimo.core.model.RunningHabit
import com.githukudenis.intimo.core.util.UserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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

    val userMessageList: MutableStateFlow<List<UserMessage>> = MutableStateFlow(emptyList())

    val uiState: StateFlow<ActiveHabitUiState> = combine(
        habitsRepository.runningHabits,
        timerState,
        userMessageList
    ) { runningHabits, timerState, userMessages, ->

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
            ),
            userMessages = userMessages
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

    fun showUserMessage(userMessage: UserMessage) {
        userMessageList.update {
            it.apply {
                this.toMutableList().add(userMessage)
            }
        }
    }

    fun dismissMessage(messageId: Long) {
        val messages = userMessageList.value.filterNot { it.id == messageId }
        userMessageList.update { messages }
    }

}