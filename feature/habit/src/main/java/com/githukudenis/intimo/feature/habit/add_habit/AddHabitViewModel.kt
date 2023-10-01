package com.githukudenis.intimo.feature.habit.add_habit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.intimo.core.data.repository.HabitsRepository
import com.githukudenis.intimo.core.model.DurationType
import com.githukudenis.intimo.core.model.HabitData
import com.githukudenis.intimo.core.util.UserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddHabitViewModel @Inject constructor(
    private val habitsRepository: HabitsRepository
): ViewModel() {
    var uiState: MutableStateFlow<AddHabitUiState> = MutableStateFlow(AddHabitUiState())
        private set

    fun onEvent(event: AddHabitUiEvent) {
        when(event) {
            is AddHabitUiEvent.ChangeHabitDays -> {
                uiState.update {  state ->
                    state.copy(days = event.habitDays)
                }
            }
            is AddHabitUiEvent.ChangeHabitDuration -> {
                uiState.update { state ->
                    state.copy(
                        habitDuration = event.duration,
                        habitDurationType = if (event.duration >= 1000L * 60 * 60) DurationType.HOUR else DurationType.MINUTE
                    )
                }
            }
            is AddHabitUiEvent.ChangeHabitFrequency -> {
                uiState.update { state ->
                    state.copy(habitFrequency = event.habitFrequency)
                }
            }
            is AddHabitUiEvent.ChangeHabitName -> {
                uiState.update { state ->
                    state.copy(habitName = event.habitName)
                }
            }
            is AddHabitUiEvent.ChangeHabitStartTime -> {
                uiState.update { state ->
                    state.copy(startTime = event.startTime)
                }
            }
            is AddHabitUiEvent.ChangeRemindTime -> {
                uiState.update { state ->
                    state.copy(remindTime = event.remindTime)
                }
            }

            is AddHabitUiEvent.ChangeHabitIcon -> {
                uiState.update { state ->
                    state.copy(habitIcon = event.icon)
                }
            }

            is AddHabitUiEvent.ShowUserMessage -> {
                val messages = uiState.value.userMessages.toMutableList()
                messages.add(UserMessage(message = event.message))
                uiState.update { state ->
                    state.copy(userMessages = messages)
                }
            }

            is AddHabitUiEvent.DismissUserMessage -> {
                val messages = uiState.value.userMessages.filterNot { it.id == event.id }
                uiState.update { state ->
                    state.copy(userMessages = messages)
                }
            }

            AddHabitUiEvent.SaveHabit -> {
                saveHabit()
            }
        }
    }

    private fun saveHabit() {
        viewModelScope.launch {
            val (habitName, habitIcon, startTime, habitDuration, durationType, habitFrequency, habitDays, remindTime) = uiState.value
            val habit = HabitData(
                habitIcon = habitIcon,
                habitName = habitName,
                startTime = startTime,
                duration = habitDuration,
                durationType = durationType,
                habitFrequency = habitFrequency,
                habitDays = habitDays,
                remindTime = remindTime
            )
            habitsRepository.insertHabit(habit)
            val messages = uiState.value.userMessages
            messages.toMutableList().add(
                UserMessage(
                    message = "New habit added successfully"
                )
            )
            uiState.update { state -> state.copy(userMessages = messages) }
        }
    }
}