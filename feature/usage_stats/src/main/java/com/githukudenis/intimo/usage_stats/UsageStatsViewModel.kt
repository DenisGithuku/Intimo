package com.githukudenis.intimo.usage_stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.data.repository.UsageStatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class UsageStatsViewModel @Inject constructor(
    usageStatsRepository: UsageStatsRepository
) : ViewModel() {

    val uiState: StateFlow<UsageStatsUiState> = usageStatsRepository.queryAndAggregateUsageStats(
        date = LocalDate.now()
    ).map { usageStats ->
        UsageStatsUiState.Loaded(
            usageStats = usageStats,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UsageStatsUiState.Loading
    )

    fun onRetry() {

    }
}