package com.githukudenis.intimo.core.datastore


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.githukudenis.intimo.core.model.Theme
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
            theme = Theme.valueOf(prefs[PreferenceKeys.theme] ?: Theme.SYSTEM.name.uppercase())
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

    suspend fun setAppTheme(theme: Theme) {
        userPreferences.edit { preferences ->
            preferences[PreferenceKeys.theme] = theme.name
        }
    }
}

object PreferenceKeys {
    val shouldHideOnBoarding = booleanPreferencesKey("should_hide_onboarding")
    val deviceUsageNotificationsAllowed = booleanPreferencesKey("device_notifications_allowed")
    val habitNotificationsAllowed = booleanPreferencesKey("habit_notifications_allowed")
    val theme = stringPreferencesKey("app_theme")
}