package com.githukudenis.onboarding.ui

data class OnBoardingUiState(
    val availableHabits: List<Habit> = emptyList(),
    val selectedHabits: List<Habit> = emptyList(),
    val isLoading: Boolean = false
) {
    val uiIsValid: Boolean get() = selectedHabits.isNotEmpty()
}