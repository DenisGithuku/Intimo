package com.githukudenis.summary.ui

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.data.repository.IntimoUsageStatsRepository
import com.githukudenis.model.ApplicationInfoData
import com.githukudenis.model.DataUsageStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val intimoUsageStatsRepository: IntimoUsageStatsRepository
) : ViewModel() {

    var uiState: MutableStateFlow<SummaryUiState> = MutableStateFlow(SummaryUiState.Loading)
        private set

    var queryDetails = MutableStateFlow(QueryTime())
        private set

    init {
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.set(Calendar.HOUR, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        queryDetails.update {
            it.copy(
                beginTime = startTime,
                endTime = endTime,
                interval = UsageStatsManager.INTERVAL_BEST
            )
        }
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

            intimoUsageStatsRepository.queryAndAggregateUsageStats(
                beginTime = System.currentTimeMillis() - 24 * 60 * 60 * 1000,
                endTime = System.currentTimeMillis()
            )
                .map { usageStats ->
                    Log.d("usage", usageStats.appUsageList.toString())
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
    val usageStats: DataUsageStats
)

data class QueryTime(
    val beginTime: Long = 0L,
    val endTime: Long = 0L,
    val interval: Int = 0
)