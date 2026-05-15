package com.hasiru.usiru.mapper.presentation.pit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasiru.usiru.mapper.core.util.ImageCompressor
import com.hasiru.usiru.mapper.domain.ai.SpeciesIdentificationService
import com.hasiru.usiru.mapper.domain.model.*
import com.hasiru.usiru.mapper.domain.repository.AuthRepository
import com.hasiru.usiru.mapper.domain.repository.PitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReportPitUiState(
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val soilType: SoilType = SoilType.LOAMY,
    val pitWidth: String = "",
    val pitDepth: String = "",
    val water: WaterAvailability = WaterAvailability.MODERATE,
    val sunlight: SunlightExposure = SunlightExposure.PARTIAL,
    val conditions: String = "",
    val recommendedSpecies: List<String> = emptyList(),
    val imageBytes: ByteArray? = null,
    val isSaving: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ReportPitViewModel @Inject constructor(
    private val pitRepository: PitRepository,
    private val authRepository: AuthRepository,
    private val aiService: SpeciesIdentificationService,
    private val imageCompressor: ImageCompressor
) : ViewModel() {

    private val _state = MutableStateFlow(ReportPitUiState())
    val state = _state.asStateFlow()

    fun initLocation(lat: Double, lng: Double) = _state.update { it.copy(lat = lat, lng = lng) }
    fun setImage(bytes: ByteArray) = _state.update { it.copy(imageBytes = imageCompressor.compress(bytes)) }
    fun updateWidth(v: String) = _state.update { it.copy(pitWidth = v) }
    fun updateDepth(v: String) = _state.update { it.copy(pitDepth = v) }
    fun updateConditions(v: String) = _state.update { it.copy(conditions = v) }
    fun updateSoil(s: SoilType) = _state.update { it.copy(soilType = s) }
    fun updateWater(w: WaterAvailability) = _state.update { it.copy(water = w) }
    fun updateSunlight(s: SunlightExposure) = _state.update { it.copy(sunlight = s) }

    fun loadRecommendations() {
        viewModelScope.launch {
            val s = _state.value
            val profile = authRepository.getProfile()
            val rec = aiService.recommendSpeciesForPit(
                s.soilType.name, s.water.name, s.sunlight.name, profile?.city ?: "Bengaluru"
            )
            _state.update { it.copy(recommendedSpecies = rec) }
        }
    }

    fun save(onDone: () -> Unit) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isSaving = true) }
                val profile = authRepository.getProfile()
                var s = _state.value
                if (s.recommendedSpecies.isEmpty()) {
                    val rec = aiService.recommendSpeciesForPit(
                        s.soilType.name, s.water.name, s.sunlight.name, profile?.city ?: "Bengaluru"
                    )
                    s = s.copy(recommendedSpecies = rec)
                    _state.update { it.copy(recommendedSpecies = rec) }
                }
                val pit = EmptyPit(
                    userId = profile?.uid ?: "",
                    userName = profile?.name ?: "Citizen",
                    latitude = s.lat,
                    longitude = s.lng,
                    soilType = s.soilType,
                    pitWidthCm = s.pitWidth.toDoubleOrNull() ?: 60.0,
                    pitDepthCm = s.pitDepth.toDoubleOrNull() ?: 60.0,
                    waterAvailability = s.water,
                    sunlightExposure = s.sunlight,
                    nearbyConditions = s.conditions,
                    recommendedSpecies = s.recommendedSpecies,
                    city = profile?.city ?: "Bengaluru"
                )
                pitRepository.savePit(pit, s.imageBytes)
                    .onSuccess { onDone() }
                    .onFailure { e -> _state.update { it.copy(isSaving = false, error = e.message) } }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }
}
