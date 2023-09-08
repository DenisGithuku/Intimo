package com.githukudenis.data.repository


import com.githukudenis.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

    /*
    User data stream
     */
    val userData: Flow<UserData>

    /*
    Set whether user has completed onboarding
     */
    suspend fun setShouldHideOnBoarding(shouldHideOnBoarding: Boolean)
    suspend fun updateHabitTime(habitTime: Long)
}