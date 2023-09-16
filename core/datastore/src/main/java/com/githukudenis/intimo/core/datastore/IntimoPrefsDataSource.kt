package com.githukudenis.intimo.core.datastore


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.githukudenis.intimo.core.model.UserData
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class IntimoPrefsDataSource @Inject constructor(
    private val userPreferences: DataStore<Preferences>
) {
    val userData = userPreferences.data.map { prefs ->
        UserData(
            shouldHideOnBoarding = prefs[PreferenceKeys.shouldHideOnBoarding] ?: false,
            deviceUsageNotificationsAllowed = prefs[PreferenceKeys.deviceUsageNotificationsAllowed] ?: false,
            habitNotificationsAllowed = prefs[PreferenceKeys.habitNotificationsAllowed] ?: false,
            systemInDarkTheme = prefs[PreferenceKeys.systemInDarkTheme] ?: false
        )
    }

    suspend fun setShouldHideOnBoarding(shouldHideOnBoarding: Boolean) {
        userPreferences.edit { preferences ->
            preferences[PreferenceKeys.shouldHideOnBoarding] = shouldHideOnBoarding
        }
    }

    suspend fun setShouldAllowDeviceNotifications(shouldAllowDeviceNotifications: Boolean) {
        userPreferences.edit { preferences ->
            preferences[PreferenceKeys.deviceUsageNotificationsAllowed] = shouldAllowDeviceNotifications
        }
    }

    suspend fun setShouldAllowHabitNotifications(shouldAllowHabitNotifications: Boolean) {
        userPreferences.edit { preferences ->
            preferences[PreferenceKeys.habitNotificationsAllowed] = shouldAllowHabitNotifications
        }
    }

    suspend fun setDarkTheme(systemInDarkTheme: Boolean) {
        userPreferences.edit { preferences ->
            preferences[PreferenceKeys.systemInDarkTheme] = systemInDarkTheme

        }
    }
}

object PreferenceKeys {
    val shouldHideOnBoarding = booleanPreferencesKey("should_hide_onboarding")
    val deviceUsageNotificationsAllowed = booleanPreferencesKey("device_notifications_allowed")
    val habitNotificationsAllowed = booleanPreferencesKey("habit_notifications_allowed")
    val systemInDarkTheme = booleanPreferencesKey("system_in_dark_theme")
}