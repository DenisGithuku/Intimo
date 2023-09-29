package com.githukudenis.intimo.feature.onboarding.ui.habit_selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githukudenis.intimo.core.model.DefaultHabit
import com.githukudenis.intimo.core.model.DurationType
import com.githukudenis.intimo.core.model.HabitFrequency
import com.githukudenis.intimo.feature.onboarding.ui.components.GetStartedBtn
import com.githukudenis.intimo.feature.onboarding.ui.components.HabitItem
import com.githukudenis.intimo.feature.onboarding.ui.components.OnBoardingTitle


@Composable
fun OnBoardingRoute(
    onFinishedOnBoarding: () -> Unit, onBoardingViewModel: OnBoardingViewModel = hiltViewModel()
) {

    val uiState by onBoardingViewModel.onBoardingUiState.collectAsStateWithLifecycle()

    OnBoardingContent(isLoading = uiState.isLoading,
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
    Box(modifier = Modifier.safeDrawingPadding()) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalItemSpacing = 14.dp,
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
            )
        ) {
            item(
                span = StaggeredGridItemSpan.FullLine
            ) {
                OnBoardingTitle()
            }
            habitItemList(
                defaultHabitList = defaultHabitList,
                selectedDefaultHabits = selectedDefaultHabits,
                onToggleHabit = onToggleHabit
            )
            item(span = StaggeredGridItemSpan.FullLine
            ) {
                GetStartedBtn(uiIsValid = uiIsValid, onGetStarted = onGetStarted)
            }
        }
    }
}

private fun LazyStaggeredGridScope.habitItemList(
    defaultHabitList: List<DefaultHabit>,
    selectedDefaultHabits: List<DefaultHabit>,
    onToggleHabit: (DefaultHabit) -> Unit
) {
    items(items = defaultHabitList, key = { it.habitName }) { habit ->
        HabitItem(emoji = habit.icon,
            description = habit.habitName,
            selected = habit in selectedDefaultHabits,
            onToggle = {
                onToggleHabit(habit)
            })
    }
}

@Preview
@Composable
fun OnBoardingRoutePrev() {
    OnBoardingContent(isLoading = false, defaultHabitList = listOf(
        DefaultHabit(
            icon = "\uD83D\uDE0E",
            habitName = "Take a nap",
            selected = true,
            durationType = DurationType.MINUTE,
            habitFrequency = HabitFrequency.DAILY,
            habitDays = listOf()

        ),
        DefaultHabit(
            icon = "\uD83D\uDE0E",
            habitName = "Read a book",
            selected = true,
            durationType = DurationType.MINUTE,
            habitFrequency = HabitFrequency.DAILY,
            habitDays = listOf()

        ),
        DefaultHabit(
            icon = "\uD83D\uDE0E",
            habitName = "Floss",
            selected = true,
            durationType = DurationType.MINUTE,
            habitFrequency = HabitFrequency.DAILY,
            habitDays = listOf()

        ),
        DefaultHabit(
            icon = "\uD83D\uDE0E",
            habitName = "Journal your thoughts",
            selected = true,
            durationType = DurationType.MINUTE,
            habitFrequency = HabitFrequency.DAILY,
            habitDays = listOf()

        ),
    ), selectedDefaultHabits = listOf(
        DefaultHabit(
            icon = "\uD83D\uDE0E",
            habitName = "Read a book",
            selected = true,
            durationType = DurationType.MINUTE,
            habitFrequency = HabitFrequency.DAILY,
            habitDays = listOf()

        ),
        DefaultHabit(
            icon = "\uD83D\uDE0E",
            habitName = "Floss",
            selected = true,
            durationType = DurationType.MINUTE,
            habitFrequency = HabitFrequency.DAILY,
            habitDays = listOf()

        ),
    ), uiIsValid = true, onToggleHabit = {}) {

    }
}
