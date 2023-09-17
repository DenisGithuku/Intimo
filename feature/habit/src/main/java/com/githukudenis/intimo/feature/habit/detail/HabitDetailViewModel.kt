package com.githukudenis.intimo.feature.habit.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.intimo.core.data.repository.HabitsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HabitDetailViewModel @Inject constructor(
    private val habitsRepository: HabitsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(HabitDetailUiState())
    val uiState: StateFlow<HabitDetailUiState> get() = _uiState.asStateFlow()

    private val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    init {
        savedStateHandle.get<Long>("habitId")?.let { habitId ->
            _uiState.update {
                it.copy(habitId = habitId, selectedDate = today)
            }
            getHabitDetails(habitId)
        }
    }

    private fun getHabitDetails(habitId: Long) {
        viewModelScope.launch {
            val completedHabits = habitsRepository.completedHabitList
            val runningHabits = habitsRepository.runningHabits
            val habit = habitsRepository.getHabitById(habitId)
            combine(completedHabits, runningHabits) { completed, running ->
                _uiState.update { habitDetailState ->
                    habitDetailState.copy(
                        habitUiModel = habit.toHabitUiModel(
                            completed = habitId in completed.filter { it.day.dayId == today }
                                .flatMap { it.habits }
                                .map { it.habitId },

                            ).copy(
                            running = running.any { it.habitId == habit.habitId }
                        ),
                        completedDayList = completed.filter { dayAndHabits ->
                            dayAndHabits.habits.any { it.habitId == habitId }
                        }.map {
                            Calendar.getInstance().apply { timeInMillis = it.day.dayId }
                                .toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                    )
                }
            }.launchIn(viewModelScope)
        }
    }

    fun onUpdate(habitUiModel: HabitUiModel) {
        viewModelScope.launch {
            habitsRepository.updateHabit(
                habitUiModel.toHabitData()
            )
        }
    }
}