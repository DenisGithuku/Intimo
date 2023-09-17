package com.githukudenis.intimo.feature.usage_stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.intimo.core.data.repository.AppsUsageRepository
import com.githukudenis.intimo.core.data.repository.UsageStatsRepository
import com.githukudenis.intimo.core.model.AppInFocusMode
import com.githukudenis.intimo.core.util.UserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class UsageStatsViewModel @Inject constructor(
    usageStatsRepository: UsageStatsRepository,
    private val appsUsageRepository: AppsUsageRepository
) : ViewModel() {

    private val userMessages = MutableStateFlow(emptyList<UserMessage>())

    val uiState: StateFlow<UsageStatsUiState> = combine(
        usageStatsRepository.queryAndAggregateUsageStats(
            date = LocalDate.now()
        ),
        appsUsageRepository.appsInFocusMode,
        userMessages
    ) { usageStats, appsInFocusMode, userMessages ->
        UsageStatsUiState.Loaded(
            usageStats = usageStats,
            appsInFocusMode = appsInFocusMode,
            userMessages = userMessages
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
                    appsUsageRepository.updateAppInFocusMode(appInFocusMode.copy(packageName = packageName, limitDuration = duration))
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