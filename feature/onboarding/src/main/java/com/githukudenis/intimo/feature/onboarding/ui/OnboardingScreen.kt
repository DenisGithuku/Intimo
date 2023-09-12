package com.githukudenis.intimo.feature.onboarding.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githukudenis.intimo.core.model.DefaultHabit
import com.githukudenis.intimo.core.model.DurationType
import com.githukudenis.intimo.core.model.HabitType
import com.githukudenis.intimo.core.model.nameToString
import com.githukudenis.intimo.feature.onboarding.ui.components.GetStartedBtn
import com.githukudenis.intimo.feature.onboarding.ui.components.HabitItem
import com.githukudenis.intimo.feature.onboarding.ui.components.OnBoardingTitle


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
    Box(modifier = Modifier.safeDrawingPadding()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
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

@Preview
@Composable
fun OnBoardingRoutePrev() {
    OnBoardingContent(
        isLoading = false,
        defaultHabitList = listOf(
            DefaultHabit(
                icon = "\uD83D\uDE0E",
                habitType = HabitType.DECLUTTERRING,
                selected = true,
                durationType = DurationType.MINUTE

            ),
            DefaultHabit(
                icon = "\uD83E\uDD14", habitType = HabitType.FLOSSING, selected = false,
                durationType = DurationType.MINUTE

            ),
            DefaultHabit(
                icon = "\uD83D\uDE0E", habitType = HabitType.JOURNALING, selected = false,
                durationType = DurationType.MINUTE
            ),
            DefaultHabit(
                icon = "✍️", habitType = HabitType.REFLECTION, selected = true,
                durationType = DurationType.MINUTE
            )
        ),
        selectedDefaultHabits = listOf(
            DefaultHabit(
                icon = "\uD83D\uDE0E", habitType = HabitType.JOURNALING, selected = false,
                durationType = DurationType.MINUTE
            ),
            DefaultHabit(
                icon = "✍️", habitType = HabitType.REFLECTION, selected = true,
                durationType = DurationType.MINUTE
            )
        ),
        uiIsValid = true,
        onToggleHabit = {}
    ) {

    }
}
