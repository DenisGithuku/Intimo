package com.githukudenis.datastore


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.githukudenis.model.UserData
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class IntimoPrefsDataSource @Inject constructor(
    private val userPreferences: DataStore<Preferences>
) {
    val userData = userPreferences.data.map { prefs ->
        UserData(
            shouldHideOnBoarding = prefs[PreferenceKeys.shouldHideOnBoarding] ?: false,
            notificationCount = prefs[PreferenceKeys.notificationCount] ?: 0L,
            remainingHabitTime = prefs[PreferenceKeys.habitTime] ?: 0L
        )
    }

    suspend fun setShouldHideOnBoarding(shouldHideOnBoarding: Boolean) {
        userPreferences.edit { preferences ->
            preferences[PreferenceKeys.shouldHideOnBoarding] = shouldHideOnBoarding
        }
    }

    suspend fun storeNotificationCount(notificationCount: Long) {
        userPreferences.edit { preferences ->
            preferences[PreferenceKeys.notificationCount] = notificationCount + 1
        }
    }

    suspend fun setHabitTime(habitTime: Long) {
        userPreferences.edit { preferences ->
            preferences[PreferenceKeys.habitTime] = habitTime
        }
    }
}

object PreferenceKeys {
    val shouldHideOnBoarding = booleanPreferencesKey("should_hide_onboarding")
    val notificationCount = longPreferencesKey("notification_count")
    val habitTime = longPreferencesKey("remaining_time")
}