package com.githukudenis.intimo.habit.active

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.data.repository.HabitsRepository
import com.githukudenis.model.RunningHabit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
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

    private var _uiState = MutableStateFlow(ActiveHabitUiState())
    val uiState: StateFlow<ActiveHabitUiState> get() = _uiState.asStateFlow()


    val runningHabit = habitsRepository.runningHabit?.onEach { runningHabit ->
        val timerState = _uiState.value.timerState.copy(
                totalTime = runningHabit.totalTime,
                currentTime = runningHabit.remainingTime,
            )
        _uiState.update {
            it.copy(timerState = timerState)
        }
    }?.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RunningHabit()
    )



    init {
        savedStateHandle.get<Long>("habitId")?.let { habitId ->
            getHabitDetails(habitId)
        }
        Log.d("habit_time", runningHabit?.value.toString())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getHabitDetails(habitId: Long) {
        viewModelScope.launch {
            val habitData = habitsRepository.getHabitById(habitId)


            _uiState.update { state ->
                state.copy(
                    habitId = habitId,
                    habitData = habitData,
                    timerState = state.timerState.copy(
                        totalTime = habitData.duration,
                        currentTime = habitData.duration
                    )
                )
            }

        }
    }

//    fun onStartTimer(duration: Long) {
//        val state = _uiState.value
//
//        state.timerState.currentTime?.let { currTime ->
//
//            val updatedTimerState = state.timerState.copy(
//                isRunning = !state.timerState.isRunning
//            )
//
//            _uiState.update {
//                it.copy(timerState = updatedTimerState)
//            }
//        }
//        viewModelScope.launch {
//            habitsRepository.updateRunningHabit(duration)
//        }
//    }

    fun onTimeChanged(time: Long) {
        _uiState.update { state ->
            val timerState = state.timerState.copy(currentTime = time)
            state.copy(timerState = timerState)
        }
    }

    fun onTimerFinished() {
        viewModelScope.launch {
            val dayId = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            _uiState.value.habitId?.let { habitId ->
                habitsRepository.completeHabit(
                    dayId = dayId,
                    habitId = habitId
                )
            }

        }
    }
}