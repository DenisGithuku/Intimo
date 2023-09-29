package com.githukudenis.intimo.feature.onboarding.ui.habit_selection

import com.githukudenis.intimo.core.model.DefaultHabit

sealed class OnBoardingEvent {
    data class AddHabit(val defaultHabit: DefaultHabit) : OnBoardingEvent()
    object GetStarted: OnBoardingEvent()
}