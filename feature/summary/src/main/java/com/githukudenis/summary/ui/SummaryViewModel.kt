package com.githukudenis.summary.ui

import android.app.usage.UsageStats
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.data.repository.IntimoUsageStatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val intimoUsageStatsRepository: IntimoUsageStatsRepository
) : ViewModel() {

    var uiState: MutableStateFlow<SummaryUiState> = MutableStateFlow(SummaryUiState.Loading)
        private set

    init {
        getUsageStats()
    }

    fun onEvent(event: SummaryUiEvent) {
        when (event) {
            SummaryUiEvent.Refresh -> {
                getUsageStats()
            }

            is SummaryUiEvent.ShowError -> {
                when(val currentState = uiState.value) {
                    is SummaryUiState.Error -> {
                        val userErrorList = mutableListOf<UserError>()
                        userErrorList.add(event.error)
                        val newCurrentState = currentState.copy(userErrorList = userErrorList)
                        uiState.update {
                           newCurrentState
                        }
                    }
                    is SummaryUiState.Success -> {
                        val userErrorList = mutableListOf<UserError>()
                        userErrorList.add(event.error)
                        val newCurrentState = currentState.copy(userErrorList = userErrorList)
                        uiState.update {
                            newCurrentState
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun getUsageStats() {
        viewModelScope.launch {
            intimoUsageStatsRepository.queryAndAggregateUsageStats()
                .map { usageStats ->
                    SummaryUiState.Success(
                        SummaryData(usageStats = usageStats)
                    )
                }
                .collect { summaryUiState ->
                    uiState.value = summaryUiState
                }
        }
    }

}

sealed interface SummaryUiEvent {
    object Refresh : SummaryUiEvent
    data class ShowError(val error: UserError) : SummaryUiEvent
}

sealed interface SummaryUiState {
    object Loading : SummaryUiState
    data class Success(
        val summaryData: SummaryData,
        val userErrorList: List<UserError> = emptyList()
    ) : SummaryUiState

    data class Error(val userErrorList: List<UserError> = emptyList()) : SummaryUiState

}

data class SummaryData(
    val usageStats: Map<String, UsageStats>
)