package com.githukudenis.intimo.core.data.repository


import com.githukudenis.intimo.core.model.UserData
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
}