package com.hasiru.usiru.mapper.presentation.species

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasiru.usiru.mapper.R
import com.hasiru.usiru.mapper.domain.model.SpeciesInfo
import com.hasiru.usiru.mapper.domain.repository.SpeciesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SpeciesViewModel @Inject constructor(private val repository: SpeciesRepository) : ViewModel() {
    private val _query = MutableStateFlow("")
    val species: StateFlow<List<SpeciesInfo>> = _query
        .debounce(300)
        .flatMapLatest { q ->
            if (q.isBlank()) repository.observeSpecies()
            else flow { emit(repository.searchSpecies(q)) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun search(q: String) { _query.value = q }
    fun toggleFavorite(id: String) { viewModelScope.launch { repository.toggleFavorite(id) } }
}

@Composable
fun SpeciesGuideScreen(viewModel: SpeciesViewModel = hiltViewModel()) {
    val species by viewModel.species.collectAsState()
    var query by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(stringResource(R.string.species_guide), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it; viewModel.search(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.search_species)) },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(species, key = { it.id }) { sp ->
                SpeciesCard(sp, onFavorite = { viewModel.toggleFavorite(sp.id) })
            }
        }
    }
}

@Composable
fun SpeciesCard(species: SpeciesInfo, onFavorite: () -> Unit) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text(species.nameEn, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${species.nameKn} • ${species.scientificName}", style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = onFavorite) {
                    Icon(
                        if (species.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Text("O₂ Factor: ${species.oxygenFactor}", style = MaterialTheme.typography.labelLarge)
            Text(species.descriptionEn, style = MaterialTheme.typography.bodySmall, maxLines = 2)
            Text(species.descriptionKn, style = MaterialTheme.typography.bodySmall, maxLines = 2)
        }
    }
}
