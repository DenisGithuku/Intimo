package com.githukudenis.onboarding.ui

import com.githukudenis.model.DefaultHabit

data class OnBoardingUiState(
    val availableDefaultHabits: List<DefaultHabit> = emptyList(),
    val selectedDefaultHabits: List<DefaultHabit> = emptyList(),
    val isLoading: Boolean = false
) {
    val uiIsValid: Boolean get() = selectedDefaultHabits.isNotEmpty()
}