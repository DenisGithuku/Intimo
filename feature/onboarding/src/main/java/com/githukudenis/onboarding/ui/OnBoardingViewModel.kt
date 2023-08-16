package com.githukudenis.onboarding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.data.repository.HabitsRepository
import com.githukudenis.data.repository.UserDataRepository
import com.githukudenis.model.Day
import com.githukudenis.model.DefaultHabit
import com.githukudenis.model.HabitData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val habitsRepository: HabitsRepository
) : ViewModel() {

    var onBoardingUiState = MutableStateFlow(OnBoardingUiState())
        private set

    init {
        onBoardingUiState.update { currentState ->
            currentState.copy(availableDefaultHabits = habitsRepository.availableHabitList)
        }
    }


    private fun setShouldHideOnBoarding() {
        viewModelScope.launch {
            userDataRepository.setShouldHideOnBoarding(true)
        }
    }

    fun handleOnBoardingEvent(onBoardingEvent: OnBoardingEvent) {
        when (onBoardingEvent) {
            is OnBoardingEvent.AddHabit -> {
                addHabit(onBoardingEvent.defaultHabit)
            }

            OnBoardingEvent.GetStarted -> {
                storeHabits()
            }
        }
    }

    private fun addHabit(defaultHabit: DefaultHabit) {
        val selectedHabits = onBoardingUiState.value.selectedDefaultHabits.toMutableList()
        if (selectedHabits.contains(defaultHabit)) {
            selectedHabits.remove(defaultHabit)
        } else {
            selectedHabits.add(defaultHabit)
        }
        onBoardingUiState.update { currentState ->
            currentState.copy(
                selectedDefaultHabits = selectedHabits
            )
        }
    }

    private fun storeHabits() {
        onBoardingUiState.update {
            it.copy(
                isLoading = true
            )
        }

        viewModelScope.launch {
            val date = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            habitsRepository.insertDay(day = Day(date))
            val habits = onBoardingUiState.value.selectedDefaultHabits
                .map { habit ->
                    HabitData(
                        habitIcon = habit.icon,
                        habitType = habit.habitType,
                        startTime = habit.startTime,
                        duration = habit.duration
                    )
                }
            habitsRepository.insertHabit(*habits.toTypedArray())

        }
        onBoardingUiState.update {
            it.copy(isLoading = false)
        }
        setShouldHideOnBoarding()
    }
}



