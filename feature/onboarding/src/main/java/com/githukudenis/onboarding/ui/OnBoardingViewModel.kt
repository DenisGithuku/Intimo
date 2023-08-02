package com.githukudenis.onboarding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.data.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
) : ViewModel() {

    var onBoardingUiState = MutableStateFlow(OnBoardingUiState())
        private set

    init {
        val habitList = listOf(
            Habit(
                emoji = "\uD83D\uDCDA",
                description = "Reading",
            ),
            Habit(
                emoji = "\uD83E\uDDD8",
                description = "Meditation",
            ),
            Habit(
                emoji = "\uD83D\uDECC",
                description = "Sleep",
            ),
            Habit(
                emoji = "✍️",
                description = "Journaling",
            ),
            Habit(
                emoji = "\uD83C\uDFC3",
                description = "Exercise",
            ),
            Habit(
                emoji = "\uD83E\uDD14",
                description = "Reflect",
            ),
            Habit(
                emoji = "\uD83C\uDF4E",
                description = "Nutrition",
            ),
            Habit(
                emoji = "\uD83E\uDD38",
                description = "Stretching",
            )

            )
        onBoardingUiState.update { currentState ->
            currentState.copy(availableHabits = habitList)
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
                addHabit(onBoardingEvent.habit)
            }

            OnBoardingEvent.HideOnBoarding -> {
                setShouldHideOnBoarding()
            }
        }
    }

    private fun addHabit(habit: Habit) {
        val selectedHabits = onBoardingUiState.value.selectedHabits.toMutableList()
        if (selectedHabits.contains(habit)) {
            selectedHabits.remove(habit)
        } else {
            selectedHabits.add(habit)
        }
        onBoardingUiState.update { currentState ->
            currentState.copy(
                selectedHabits = selectedHabits
            )
        }
    }
}

data class OnBoardingUiState(
    val availableHabits: List<Habit> = emptyList(),
    val selectedHabits: List<Habit> = emptyList()
) {
    val uiIsValid: Boolean get() = selectedHabits.isNotEmpty()
}

data class Habit(
    val emoji: String,
    val description: String,
    val selected: Boolean = false
)

sealed class OnBoardingEvent {
    data class AddHabit(val habit: Habit) : OnBoardingEvent()
    object HideOnBoarding : OnBoardingEvent()
}