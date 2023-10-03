package com.githukudenis.intimo.feature.habit.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.intimo.core.data.repository.HabitsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HabitStatsViewModel @Inject constructor(
    private val habitsRepository: HabitsRepository
) : ViewModel() {

    val uiState: StateFlow<HabitStatsUiState>
        get() = combine(
            habitsRepository.completedHabitList,
            habitsRepository.availableHabitList
        ) { completed, available ->
            val completionRate = completed.map {
//                Pair(
//                    it.day,
//                    it.habits.any {
//
//                    }
//                )
            }

            HabitStatsUiState(

            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = HabitStatsUiState()
            )

}