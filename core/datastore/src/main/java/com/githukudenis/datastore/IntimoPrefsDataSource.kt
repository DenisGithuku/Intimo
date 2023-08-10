package com.githukudenis.datastore


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.githukudenis.model.UserData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class IntimoPrefsDataSource @Inject constructor(
    private val userPreferences: DataStore<Preferences>
) {
    val userData = userPreferences.data.map { prefs ->
        UserData(
            shouldHideOnBoarding = prefs[PreferenceKeys.shouldHideOnBoarding] ?: false,
            notificationCount = prefs[PreferenceKeys.notificationCount] ?: 0L
        )
    }

    suspend fun setShouldHideOnBoarding(shouldHideOnBoarding: Boolean) {
        userPreferences.edit { preferences ->
            preferences[PreferenceKeys.shouldHideOnBoarding] = shouldHideOnBoarding
        }
    }

    suspend fun storeNotificationCount() {
        val notificationCount = userPreferences.data.first()[PreferenceKeys.notificationCount]
        notificationCount?.run {
            userPreferences.edit { preferences ->
                preferences[PreferenceKeys.notificationCount] = this + 1
            }
        }
    }
}

object PreferenceKeys {
    val shouldHideOnBoarding = booleanPreferencesKey("should_hide_onboarding")
    val notificationCount = longPreferencesKey("notification_count")
}