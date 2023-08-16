package com.githukudenis.summary.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.data.repository.HabitsRepository
import com.githukudenis.summary.ui.toHabitUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HabitDetailViewModel @Inject constructor(
    private val habitsRepository: HabitsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(HabitDetailUiState())
    val uiState: StateFlow<HabitDetailUiState> get() = _uiState.asStateFlow()

    init {
        savedStateHandle.get<Long>("habitId")?.let { habitId ->
            getHabitDetails(habitId)
        }
    }

    private fun getHabitDetails(habitId: Long) {
        viewModelScope.launch {
            val completedHabits = habitsRepository.completedHabitList
            val habit = habitsRepository.getHabitById(habitId)
            completedHabits.collect { completed ->
                _uiState.update { habitDetailState ->
                    habitDetailState.copy(
                        habitUiModel = habit.toHabitUiModel(
                            completed = habitId in completed.flatMap { it.habits }
                                .map { it.habitId },
                        ),
                        completedHabitList = completed.flatMap { it.habits }
                    )
                }
            }
        }
    }

    fun onHabitComplete(habitId: Long) {
        viewModelScope.launch {
            val dayId = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            habitsRepository.completeHabit(
                dayId, habitId
            )
            getHabitDetails(habitId)
        }
    }

    fun onChangeDate(date: Long) {
        viewModelScope.launch {
        }
    }
}