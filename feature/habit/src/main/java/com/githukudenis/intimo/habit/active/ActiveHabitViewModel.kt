package com.githukudenis.intimo.habit.active

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.data.repository.HabitsRepository
import com.githukudenis.data.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitActiveViewModel @Inject constructor(
    private val habitsRepository: HabitsRepository,
    private val userDataRepository: UserDataRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val timerState = MutableStateFlow(TimerState())

    val uiState: StateFlow<ActiveHabitUiState> =
        combine(
            userDataRepository.userData,
            timerState
        ) { userData, timerState ->
            val habitData =
                habitsRepository.getHabitById(requireNotNull(savedStateHandle.get<Long>("habitId")))

            ActiveHabitUiState(
                habitId = savedStateHandle.get<Long>("habitId"),
                habitData = habitData,
                timerState = timerState.copy(
                    totalTime = habitData.duration,
                    currentTime = userData.remainingHabitTime
                )
            )
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                ActiveHabitUiState()
            )

    fun onToggleTimer(isRunning: Boolean) {
        viewModelScope.launch {
            timerState.update {
                it.copy(isRunning = isRunning)
            }
        }
    }
}