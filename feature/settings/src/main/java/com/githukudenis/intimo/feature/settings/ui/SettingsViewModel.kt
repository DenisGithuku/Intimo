package com.githukudenis.intimo.feature.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.intimo.core.data.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
): ViewModel(){

    val uiState: StateFlow<SettingsUiState> = userDataRepository.userData.map { userData ->
        SettingsUiState(
            isSystemInDarkTheme = userData.systemInDarkTheme,
            deviceUsageNotificationsAllowed = userData.deviceUsageNotificationsAllowed,
            habitNotificationsAllowed = userData.habitNotificationsAllowed
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        SettingsUiState()
    )

    fun onToggleTheme(systemInDarkTheme: Boolean) {
        viewModelScope.launch {
            userDataRepository.setDarkTheme(systemInDarkTheme)
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
    val isSystemInDarkTheme: Boolean = false,
    val deviceUsageNotificationsAllowed: Boolean = false,
    val habitNotificationsAllowed: Boolean = false,
)