package com.hasiru.usiru.mapper.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasiru.usiru.mapper.domain.model.DashboardStats
import com.hasiru.usiru.mapper.domain.repository.AuthRepository
import com.hasiru.usiru.mapper.domain.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val stats: DashboardStats = DashboardStats(),
    val city: String = "Bengaluru",
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val profile = authRepository.getProfile()
            val city = profile?.city ?: "Bengaluru"
            _state.update { it.copy(city = city) }
            dashboardRepository.observeStats(city)
                .catch { e -> _state.update { it.copy(error = e.message, isLoading = false) } }
                .collect { stats ->
                    _state.update { it.copy(stats = stats, isLoading = false, isRefreshing = false) }
                }
        }
    }

    fun refresh() {
        _state.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            kotlinx.coroutines.delay(800)
            _state.update { it.copy(isRefreshing = false) }
        }
    }
}
