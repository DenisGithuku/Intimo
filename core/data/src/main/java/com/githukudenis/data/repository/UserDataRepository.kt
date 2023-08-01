package com.githukudenis.data.repository


import com.githukudenis.model.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

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