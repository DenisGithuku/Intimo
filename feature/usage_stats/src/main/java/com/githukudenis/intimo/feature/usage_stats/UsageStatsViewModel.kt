package com.githukudenis.intimo.feature.usage_stats

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.intimo.core.data.repository.AppsUsageRepository
import com.githukudenis.intimo.core.data.repository.UsageStatsRepository
import com.githukudenis.intimo.core.model.AppInFocusMode
import com.githukudenis.intimo.core.util.UserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
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

    // Create a helper function to calculate the list of dates for the selected week
    private fun getDatesForWeek(selectedDate: LocalDate): List<LocalDate> {
        val firstDayOfWeek = selectedDate.with(DayOfWeek.MONDAY)
        return (0..6).map { firstDayOfWeek.plusDays(it.toLong()) }
    }

    // Create a helper function to calculate the total weekly usage
    private suspend fun calculateWeeklyUsage(
        selectedDate: LocalDate,
    ): Long {
        val firstDayOfWeek = selectedDate.with(DayOfWeek.MONDAY)
        val weeklyUsage =  usageStatsRepository.getTotalWeeklyUsage(firstDayOfWeek).buffer().first()
        Log.d("date weekly", weeklyUsage.toString())
        return weeklyUsage
    }

    // Create a helper function to calculate daily usage values
    private suspend fun calculateDailyUsageValues(
        totalWeeklyUsage: Long,
        dateList: List<LocalDate>,
    ): List<Pair<LocalDate, Pair<Float, Long>>> {
        return dateList.map { date ->
            val usageValueListByDate =
                usageStatsRepository.queryAndAggregateUsageStats(date).buffer().first()
            val totalByDay = usageValueListByDate.appUsageList.sumOf { it.usageDuration }

            val usageValue = Pair(
                date,
                Pair(
                    totalByDay.toFloat() / totalWeeklyUsage.toFloat(),
                    totalByDay
                )
            )
            Log.d("date list", usageValue.toString())
            usageValue
        }
    }

    // UsageByDay Flow
    @OptIn(ExperimentalCoroutinesApi::class)
    private val usageByDay: Flow<List<Pair<LocalDate, Pair<Float, Long>>>> =
        selectedDate.mapLatest { selectedDate ->
            val firstDayOfWeek = selectedDate.with(DayOfWeek.MONDAY)
            val dateList = getDatesForWeek(firstDayOfWeek)
            val totalWeeklyUsage = calculateWeeklyUsage(firstDayOfWeek)
            calculateDailyUsageValues(totalWeeklyUsage, dateList)
        }


    val uiState: StateFlow<UsageStatsUiState> = combine(
        selectedDate,
        appsUsageRepository.appsInFocusMode,
        usageByDay,
        userMessages,
    ) { selectedDate, appsInFocusMode, dailyUsage, userMessages ->
        val usageStats = usageStatsRepository.queryAndAggregateUsageStats(
            date = selectedDate
        ).buffer().first()

        UsageStatsUiState.Loaded(
            usageStats = usageStats,
            appsInFocusMode = appsInFocusMode,
            userMessages = userMessages,
            chartState = ChartState(
                selectedDate = selectedDate,
                data = dailyUsage.associate {
                    it.first to Pair(it.second.first, it.second.second)
                }
            )
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
}