package com.hasiru.usiru.mapper.presentation.splash

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasiru.usiru.mapper.domain.repository.AuthRepository
import com.hasiru.usiru.mapper.domain.repository.SpeciesRepository
import com.hasiru.usiru.mapper.presentation.navigation.NavRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private val Context.onboardingStore by preferencesDataStore("onboarding")

data class SplashState(
    val isLoading: Boolean = true,
    val destination: String? = null
)

@HiltViewModel
class SplashViewModel @Inject constructor(
  @ApplicationContext private val context: Context,
  private val authRepository: AuthRepository,
  private val speciesRepository: SpeciesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SplashState())
    val state = _state.asStateFlow()

    private val onboardingKey = booleanPreferencesKey("completed")

    init {
        viewModelScope.launch {
            delay(2000)
            speciesRepository.seedDefaultSpecies()
            val destination = if (authRepository.isLoggedIn) {
                NavRoutes.MAIN
            } else {
                // Direct authentication: bypass login and go to main
                NavRoutes.MAIN
            }
            _state.value = SplashState(isLoading = false, destination = destination)
        }
    }

    suspend fun markOnboardingComplete() {
        context.onboardingStore.edit { it[onboardingKey] = true }
    }
}
