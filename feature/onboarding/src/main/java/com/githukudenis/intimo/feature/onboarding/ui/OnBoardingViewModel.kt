package com.githukudenis.intimo.feature.onboarding.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.intimo.core.data.repository.HabitsRepository
import com.githukudenis.intimo.core.data.repository.UserDataRepository
import com.githukudenis.intimo.core.model.Day
import com.githukudenis.intimo.core.model.DefaultHabit
import com.githukudenis.intimo.core.model.HabitData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val habitsRepository: HabitsRepository
) : ViewModel() {

    private val selectedHabitList = MutableStateFlow(emptyList<DefaultHabit>())

    private val isLoading = MutableStateFlow(false)

    var onBoardingUiState: StateFlow<OnBoardingUiState> = combine(
        habitsRepository.availableHabitList,
        selectedHabitList,
        isLoading
    ) { availableHabitList, selectedHabitList, isLoading ->
        val state = OnBoardingUiState(
            availableDefaultHabits = availableHabitList,
            selectedDefaultHabits = selectedHabitList,
            isLoading = isLoading
        )
        Log.d("state", state.toString())
        state

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = OnBoardingUiState()
    )


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

        selectedHabitList.update {
            selectedHabits
        }
    }

    private fun storeHabits() {
        isLoading.update {
            true
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
                        duration = habit.duration,
                        durationType = habit.durationType
                    )
                }
            habitsRepository.insertHabit(*habits.toTypedArray())

        }
        isLoading.update {
            false
        }
        setShouldHideOnBoarding()
    }
}



