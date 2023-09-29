package com.githukudenis.intimo.feature.onboarding.ui.habit_selection

import com.githukudenis.intimo.core.model.DefaultHabit

data class OnBoardingUiState(
    val availableDefaultHabits: List<DefaultHabit> = emptyList(),
    val selectedDefaultHabits: List<DefaultHabit> = emptyList(),
    val isLoading: Boolean = false
) {
    val uiIsValid: Boolean get() = selectedDefaultHabits.isNotEmpty()
}