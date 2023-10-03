package com.githukudenis.intimo.feature.settings.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.intimo.core.data.repository.UserDataRepository
import com.githukudenis.intimo.core.model.Theme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
): ViewModel(){

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<SettingsUiState> = userDataRepository.userData.mapLatest { userData ->
        SettingsUiState(
            theme = userData.theme,
            deviceUsageNotificationsAllowed = userData.deviceUsageNotificationsAllowed,
            habitNotificationsAllowed = userData.habitNotificationsAllowed
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        SettingsUiState()
    )

    fun onToggleTheme(theme: Theme) {
        viewModelScope.launch {
            userDataRepository.setAppTheme(theme)
        }
    }

    fun setShouldAllowDeviceUsageNotifications(shouldAllowDeviceUsageNotifications: Boolean) {
        viewModelScope.launch {
            userDataRepository.setShouldAllowDeviceNotifications(shouldAllowDeviceUsageNotifications)
        }
    }

    fun setShouldAllowHabitsNotifications(shouldAllowHabitsNotifications: Boolean) {
        viewModelScope.launch {
            userDataRepository.setShouldAllowHabitNotifications(shouldAllowHabitsNotifications)
        }
    }
}

data class SettingsUiState(
    val theme: Theme = Theme.SYSTEM,
    val deviceUsageNotificationsAllowed: Boolean = false,
    val habitNotificationsAllowed: Boolean = false,
)