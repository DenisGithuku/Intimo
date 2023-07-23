package com.githukudenis.onboarding.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.data.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
): ViewModel() {

    fun setShouldHideOnBoarding() {
        viewModelScope.launch {
            userDataRepository.setShouldHideOnBoarding(true)
        }
    }
}