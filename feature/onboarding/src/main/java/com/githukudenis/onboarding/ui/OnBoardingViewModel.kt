package com.githukudenis.onboarding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.data.repository.HabitsRepository
import com.githukudenis.data.repository.UserDataRepository
import com.githukudenis.model.DailyData
import com.githukudenis.model.DailyDataWithHabits
import com.githukudenis.model.HabitData
import com.githukudenis.model.HabitType
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
        val habitList = listOf(
            Habit(
                icon = "\uD83D\uDCDA",
                habitType = HabitType.READING,
            ),
            Habit(
                icon = "\uD83E\uDDD8",
                habitType = HabitType.MEDITATION,
            ),
            Habit(
                icon = "\uD83D\uDECC",
                habitType = HabitType.SLEEP,
            ),
            Habit(
                icon = "✍️",
                habitType = HabitType.JOURNALING,
            ),
            Habit(
                icon = "\uD83C\uDFC3",
                habitType = HabitType.EXERCISE,
            ),
            Habit(
                icon = "\uD83E\uDD14",
                habitType = HabitType.REFLECTION,
            ),
            Habit(
                icon = "\uD83C\uDF4E",
                habitType = HabitType.NUTRITION,
            ),
            Habit(
                icon = "\uD83E\uDD38",
                habitType = HabitType.STRETCHING,
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

            OnBoardingEvent.GetStarted -> {
                storeHabits()
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

    private fun storeHabits() {
        onBoardingUiState.update {
            it.copy(
                isLoading = true
            )
        }
        val habits = onBoardingUiState.value.selectedHabits
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val today = calendar.timeInMillis
            val dailyData = DailyData(dailyId = today)
            val habitDataList = habits
                .map { it.toHabitData() }
                .map { it.copy(dailyDataId = dailyData.dailyId) }

            habitsRepository.insertHabits(dailyData, habitDataList)
        }
        onBoardingUiState.update {
            it.copy(isLoading = false)
        }
        setShouldHideOnBoarding()
    }
}

data class Habit(
    val icon: String,
    val habitType: HabitType,
    val selected: Boolean = false
)

fun Habit.toHabitData(): HabitData {
    return HabitData(
        habitIcon = icon,
        habitType = habitType,
    )
}

