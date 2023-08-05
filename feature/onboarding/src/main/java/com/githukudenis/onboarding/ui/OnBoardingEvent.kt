package com.githukudenis.onboarding.ui

sealed class OnBoardingEvent {
    data class AddHabit(val habit: Habit) : OnBoardingEvent()
    object GetStarted: OnBoardingEvent()
}