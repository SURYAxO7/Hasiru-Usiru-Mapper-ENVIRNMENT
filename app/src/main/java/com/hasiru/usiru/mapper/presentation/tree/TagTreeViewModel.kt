package com.hasiru.usiru.mapper.presentation.tree

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasiru.usiru.mapper.core.util.ImageCompressor
import com.hasiru.usiru.mapper.domain.ai.SpeciesIdentificationService
import com.hasiru.usiru.mapper.domain.engine.OxygenScoreEngine
import com.hasiru.usiru.mapper.domain.model.*
import com.hasiru.usiru.mapper.domain.repository.AuthRepository
import com.hasiru.usiru.mapper.domain.repository.TreeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TagTreeUiState(
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val species: String = "",
    val speciesKn: String = "",
    val scientificName: String = "",
    val girthCm: String = "",
    val health: HealthCondition = HealthCondition.GOOD,
    val ageYears: String = "",
    val aiResult: AiSpeciesResult? = null,
    val imageBytes: ByteArray? = null,
    val isAnalyzing: Boolean = false,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TagTreeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val treeRepository: TreeRepository,
    private val authRepository: AuthRepository,
    private val aiService: SpeciesIdentificationService,
    private val oxygenEngine: OxygenScoreEngine,
    private val imageCompressor: ImageCompressor
) : ViewModel() {

    private val _state = MutableStateFlow(TagTreeUiState())
    val state = _state.asStateFlow()

    init {
        // lat/lng passed via navigation - set externally from screen
    }

    fun initLocation(lat: Double, lng: Double) = _state.update { it.copy(lat = lat, lng = lng) }

    fun setImage(bytes: ByteArray) {
        val compressed = imageCompressor.compress(bytes)
        _state.update { it.copy(imageBytes = compressed) }
    }

    fun updateSpecies(v: String) = _state.update { it.copy(species = v) }
    fun updateGirth(v: String) = _state.update { it.copy(girthCm = v) }
    fun updateHealth(h: HealthCondition) = _state.update { it.copy(health = h) }
    fun updateAge(v: String) = _state.update { it.copy(ageYears = v) }

    fun analyzeImage() {
        val bytes = _state.value.imageBytes ?: return
        viewModelScope.launch {
            _state.update { it.copy(isAnalyzing = true) }
            aiService.identifySpecies(bytes).onSuccess { result ->
                _state.update {
                    it.copy(
                        isAnalyzing = false,
                        aiResult = result,
                        species = result.speciesName,
                        speciesKn = result.speciesKn,
                        scientificName = result.scientificName
                    )
                }
            }.onFailure { e ->
                _state.update { it.copy(isAnalyzing = false, error = e.message) }
            }
        }
    }

    fun save(onDone: () -> Unit) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isSaving = true, error = null) }
                val profile = authRepository.getProfile()
                val s = _state.value
                val girth = s.girthCm.toDoubleOrNull() ?: 30.0
                val tree = TreeMarker(
                    userId = profile?.uid ?: "",
                    userName = profile?.name ?: "Citizen",
                    latitude = s.lat,
                    longitude = s.lng,
                    species = s.species,
                    speciesKn = s.speciesKn,
                    scientificName = s.scientificName,
                    girthCm = girth,
                    healthCondition = s.health,
                    ageEstimateYears = s.ageYears.toIntOrNull() ?: 0,
                    oxygenScore = oxygenEngine.calculateOxygenScore(girth, s.species),
                    environmentalBenefits = s.aiResult?.environmentalBenefits ?: "",
                    kannadaDescription = s.aiResult?.kannadaDescription ?: "",
                    healthSuggestions = s.aiResult?.healthSuggestions ?: "",
                    aiConfidence = s.aiResult?.confidence ?: 0f,
                    city = profile?.city ?: "Bengaluru"
                )
                treeRepository.saveTree(tree, s.imageBytes)
                    .onSuccess {
                        _state.update { it.copy(isSaving = false, saved = true) }
                        onDone()
                    }
                    .onFailure { e ->
                        _state.update { it.copy(isSaving = false, error = e.message) }
                    }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }
}
