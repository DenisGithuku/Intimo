package com.githukudenis.onboarding.ui

import com.githukudenis.model.DefaultHabit

sealed class OnBoardingEvent {
    data class AddHabit(val defaultHabit: DefaultHabit) : OnBoardingEvent()
    object GetStarted: OnBoardingEvent()
}