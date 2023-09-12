package com.githukudenis.intimo.feature.onboarding.ui

import com.githukudenis.intimo.core.model.DefaultHabit

sealed class OnBoardingEvent {
    data class AddHabit(val defaultHabit: DefaultHabit) : OnBoardingEvent()
    object GetStarted: OnBoardingEvent()
}