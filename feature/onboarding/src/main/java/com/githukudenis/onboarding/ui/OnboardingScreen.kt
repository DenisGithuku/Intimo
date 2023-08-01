package com.githukudenis.onboarding.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
        habitList = uiState.availableHabits,
        selectedHabits = uiState.selectedHabits,
        uiIsValid = uiState.uiIsValid,
        onToggleHabit = { habit ->
            onBoardingViewModel.handleOnBoardingEvent(OnBoardingEvent.AddHabit(habit))
        },
        onGetStarted = {
            onBoardingViewModel.handleOnBoardingEvent(OnBoardingEvent.HideOnBoarding)
            onFinishedOnBoarding()
        })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnBoardingContent(
    habitList: List<Habit>,
    selectedHabits: List<Habit>,
    uiIsValid: Boolean,
    onToggleHabit: (Habit) -> Unit,
    onGetStarted: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxSize()
    ) {
        OnBoardingTitle()
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            habitItemList(
                habitList = habitList,
                selectedHabits = selectedHabits,
                onToggleHabit = onToggleHabit
            )
        }
        Spacer(modifier = Modifier.height(64.dp))
        GetStartedBtn(uiIsValid = uiIsValid, onGetStarted = onGetStarted)
    }
}

private fun LazyGridScope.habitItemList(
    habitList: List<Habit>,
    selectedHabits: List<Habit>,
    onToggleHabit: (Habit) -> Unit
) {
    items(items = habitList) { habit ->
        HabitItem(
            emoji = habit.emoji,
            description = habit.description,
            selected = habit in selectedHabits,
            onToggle = {
                onToggleHabit(habit)
            }
        )
    }
}

