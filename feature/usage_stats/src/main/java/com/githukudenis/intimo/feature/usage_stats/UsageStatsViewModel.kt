package com.githukudenis.intimo.feature.usage_stats

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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class UsageStatsViewModel @Inject constructor(
    private val usageStatsRepository: UsageStatsRepository,
    private val appsUsageRepository: AppsUsageRepository
) : ViewModel() {

    private val userMessages = MutableStateFlow(emptyList<UserMessage>())

    private val selectedDate = MutableStateFlow(LocalDate.now())

    private val weeklyUsage = (0..6).map { LocalDate.now().plusDays(it.toLong()) }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val usageByDay: Flow<List<Pair<LocalDate, Pair<Float, Long>>>>
        get() = usageStatsRepository.getTotalWeeklyUsage(LocalDate.now().minusDays(6))
            .flatMapLatest { totalWeeklyUsage ->
                combine(
                    weeklyUsage.map { date ->
                        usageStatsRepository.queryAndAggregateUsageStats(date)
                            .map { it.appUsageList.map { appUsageStats -> appUsageStats.usageDuration  }.sum()}
                            .map { usageLong ->
                                Pair(date, Pair(usageLong.toFloat() / totalWeeklyUsage.toFloat(), usageLong))
                            }
                    }
                ){ it.toList() }

//                val totalDailyList: MutableList<Pair<LocalDate, Long>> = mutableListOf()
//                weeklyUsage.onEach { date ->
//                    usageStatsRepository.queryAndAggregateUsageStats(date)
//                        .collect {
//                            totalDailyList.add(
//                                Pair(
//                                    date,
//                                    it.appUsageList.map { it.usageDuration }.sum()
//                                )
//                            )
//                        }
//                }
//                totalDailyList.map { (date, usageLong) ->
//                    Pair(date, Pair(usageLong.toFloat() / totalWeeklyUsage.toFloat(), usageLong))
//                }
            }

    val uiState: StateFlow<UsageStatsUiState> = combine(
        usageStatsRepository.queryAndAggregateUsageStats(
            date = LocalDate.now()
        ),
        appsUsageRepository.appsInFocusMode,
        usageByDay,
        userMessages
    ) { usageStats, appsInFocusMode, dailyUsage, userMessages ->
        UsageStatsUiState.Loaded(
            usageStats = usageStats,
            appsInFocusMode = appsInFocusMode,
            userMessages = userMessages,
            chartData = dailyUsage.associate {
                it.first to Pair(it.second.first, it.second.second)
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
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