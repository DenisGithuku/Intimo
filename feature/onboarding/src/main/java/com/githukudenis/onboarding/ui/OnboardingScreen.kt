package com.githukudenis.onboarding.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githukudenis.model.DefaultHabit
import com.githukudenis.model.nameToString
import com.githukudenis.onboarding.ui.components.GetStartedBtn
import com.githukudenis.onboarding.ui.components.HabitItem
import com.githukudenis.onboarding.ui.components.OnBoardingTitle


@Composable
fun OnBoardingRoute(
    onFinishedOnBoarding: () -> Unit,
    onBoardingViewModel: OnBoardingViewModel = hiltViewModel()
) {

    val uiState by onBoardingViewModel.onBoardingUiState.collectAsStateWithLifecycle()

    OnBoardingContent(
        isLoading = uiState.isLoading,
        defaultHabitList = uiState.availableDefaultHabits,
        selectedDefaultHabits = uiState.selectedDefaultHabits,
        uiIsValid = uiState.uiIsValid,
        onToggleHabit = { habit ->
            onBoardingViewModel.handleOnBoardingEvent(OnBoardingEvent.AddHabit(habit))
        },
        onGetStarted = {
            onBoardingViewModel.handleOnBoardingEvent(OnBoardingEvent.GetStarted)
            onFinishedOnBoarding()
        })
}

@Composable
private fun OnBoardingContent(
    isLoading: Boolean,
    defaultHabitList: List<DefaultHabit>,
    selectedDefaultHabits: List<DefaultHabit>,
    uiIsValid: Boolean,
    onToggleHabit: (DefaultHabit) -> Unit,
    onGetStarted: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.systemBars))
        OnBoardingTitle()
        if (isLoading) {
            LinearProgressIndicator()
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            habitItemList(
                defaultHabitList = defaultHabitList,
                selectedDefaultHabits = selectedDefaultHabits,
                onToggleHabit = onToggleHabit
            )
        }
        Spacer(modifier = Modifier.height(64.dp))
        GetStartedBtn(uiIsValid = uiIsValid, onGetStarted = onGetStarted)
    }
}

private fun LazyGridScope.habitItemList(
    defaultHabitList: List<DefaultHabit>,
    selectedDefaultHabits: List<DefaultHabit>,
    onToggleHabit: (DefaultHabit) -> Unit
) {
    items(items = defaultHabitList) { habit ->
        HabitItem(
            emoji = habit.icon,
            description = habit.habitType.nameToString(),
            selected = habit in selectedDefaultHabits,
            onToggle = {
                onToggleHabit(habit)
            }
        )
    }
}

