package com.githukudenis.intimo.feature.usage_stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.intimo.core.data.repository.AppsUsageRepository
import com.githukudenis.intimo.core.data.repository.UsageStatsRepository
import com.githukudenis.intimo.core.model.AppInFocusMode
import com.githukudenis.intimo.core.util.UserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class UsageStatsViewModel @Inject constructor(
    private val usageStatsRepository: UsageStatsRepository,
    private val appsUsageRepository: AppsUsageRepository
) : ViewModel() {

    private val userMessages = MutableStateFlow(emptyList<UserMessage>())

    private val selectedDate = MutableStateFlow(LocalDate.now())

    private val chartValuesState: MutableStateFlow<LinkedHashMap<LocalDate, Pair<Float, Long>>> =
        MutableStateFlow(linkedMapOf())

    private val chartState: MutableStateFlow<ChartState> = MutableStateFlow(ChartState.Loading)

    private var usageStatsJob: Job? = null


    init {
        calculateWeeklyUsage()
    }


    // Create a helper function to calculate the list of dates for the selected week
    private fun getDatesForWeek(selectedDate: LocalDate): List<LocalDate> {
        val firstDayOfWeek = selectedDate.with(DayOfWeek.MONDAY)
        return (0..6).map { firstDayOfWeek.plusDays(it.toLong()) }
    }

    // Create a helper function to calculate the total weekly usage
    private fun calculateWeeklyUsage() {
        viewModelScope.launch {
            val firstDayOfWeek = selectedDate.value.with(DayOfWeek.MONDAY)
            val weeklyUsage =
                usageStatsRepository.getTotalWeeklyUsage(firstDayOfWeek, selectedDate.value)
            val dateList = getDatesForWeek(firstDayOfWeek)
            calculateDailyUsageValues(weeklyUsage, dateList)
        }
    }

    // Create a helper function to calculate daily usage values
    private suspend fun calculateDailyUsageValues(
        totalWeeklyUsage: Long,
        dateList: List<LocalDate>,
    ) {
        chartState.update { ChartState.Loading }

        val chartValues: LinkedHashMap<LocalDate, Pair<Float, Long>> = linkedMapOf()
        for (date in dateList) {
            val usageValueListByDate =
                usageStatsRepository.queryAndAggregateUsageStats(
                    date,
                    date
                )
            val totalByDay = usageValueListByDate.appUsageList.sumOf { it.usageDuration }

            chartValues[date] = Pair(
                totalByDay.toFloat() / totalWeeklyUsage.toFloat(),
                totalByDay
            )
        }

        chartState.update {
            ChartState.Loaded(
                selectedDate = selectedDate.value,
                data = chartValues
            )
        }

        chartValuesState.update { chartValues }
    }

    val uiState: StateFlow<UsageStatsUiState> = combine(
        appsUsageRepository.appsInFocusMode,
        chartState,
        userMessages,
    ) { appsInFocusMode, chartState, userMessages ->
        val stats = usageStatsRepository.queryAndAggregateUsageStats(
            selectedDate.value,
            selectedDate.value,
        )
        UsageStatsUiState.Loaded(
            usageStats = stats,
            appsInFocusMode = appsInFocusMode,
            userMessages = userMessages,
            chartState = chartState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UsageStatsUiState.Loading
    )

    fun onRetry() {

    }

    fun onEvent(event: UsageStatsUiEvent) {
        when (event) {
            is UsageStatsUiEvent.DismissUserMessage -> {
                val messages =
                    userMessages.value.filterNot { userMessage -> userMessage.id == event.messageId }
                userMessages.update { messages }
            }

            is UsageStatsUiEvent.LimitApp -> {
                onAddAppToFocusMode(event.packageName, event.limitDuration)
            }


            is UsageStatsUiEvent.ShowUserMessage -> {
                userMessages.update { it.toMutableList().apply { add(event.userMessage) } }
            }

            is UsageStatsUiEvent.ChangeDate -> {
                selectedDate.update { event.selectedDate }

                if (chartValuesState.value.containsKey(event.selectedDate)) {
                    chartState.update {
                        ChartState.Loaded(
                            selectedDate = event.selectedDate,
                            data = chartValuesState.value.takeIf {
                                it.containsKey(event.selectedDate)
                            } ?: return
                        )
                    }
                } else {
                    calculateWeeklyUsage()
                }
            }
        }
    }

    private fun onAddAppToFocusMode(packageName: String, duration: Long) {
        viewModelScope.launch {
            var message = ""
            val appInFocusMode =
                appsUsageRepository.appsInFocusMode.first().find { it.packageName == packageName }
            if (appInFocusMode != null) {
                if (duration == 0L) {
                    appsUsageRepository.deleteAppFromFocusMode(appInFocusMode)
                    message = "App timer deleted"
                } else {
                    appsUsageRepository.updateAppInFocusMode(
                        appInFocusMode.copy(
                            packageName = packageName,
                            limitDuration = duration
                        )
                    )
                    message = "App timer updated"
                }
            } else {
                val app = AppInFocusMode(
                    packageName = packageName,
                    limitDuration = duration
                )
                appsUsageRepository.addAppToFocusMode(app)
                message = "App timer set"
            }
            userMessages.update {
                it.toMutableList().apply { add(UserMessage(message = message)) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        usageStatsJob = null
    }
}