package com.hasiru.usiru.mapper.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.hasiru.usiru.mapper.domain.model.EmptyPit
import com.hasiru.usiru.mapper.domain.model.TreeMarker
import com.hasiru.usiru.mapper.domain.repository.PitRepository
import com.hasiru.usiru.mapper.domain.repository.TreeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapUiState(
    val trees: List<TreeMarker> = emptyList(),
    val pits: List<EmptyPit> = emptyList(),
    val selectedTree: TreeMarker? = null,
    val selectedPit: EmptyPit? = null,
    val userLocation: LatLng? = null,
    val isSatellite: Boolean = false,
    val isLoading: Boolean = true,
    val city: String = "Bengaluru"
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val treeRepository: TreeRepository,
    private val pitRepository: PitRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MapUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                treeRepository.observeTrees(_state.value.city),
                pitRepository.observePits(_state.value.city)
            ) { trees, pits ->
                _state.value.copy(trees = trees, pits = pits, isLoading = false)
            }.collect { _state.value = it }
        }
    }

    fun selectTree(tree: TreeMarker?) = _state.update { it.copy(selectedTree = tree, selectedPit = null) }
    fun selectPit(pit: EmptyPit?) = _state.update { it.copy(selectedPit = pit, selectedTree = null) }
    fun toggleSatellite() = _state.update { it.copy(isSatellite = !it.isSatellite) }
    fun updateLocation(latLng: LatLng) = _state.update { it.copy(userLocation = latLng) }
    fun clearSelection() = _state.update { it.copy(selectedTree = null, selectedPit = null) }
}
