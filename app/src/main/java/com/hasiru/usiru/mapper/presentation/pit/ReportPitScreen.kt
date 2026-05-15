package com.hasiru.usiru.mapper.presentation.pit

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hasiru.usiru.mapper.R
import com.hasiru.usiru.mapper.domain.model.SoilType
import com.hasiru.usiru.mapper.domain.model.SunlightExposure
import com.hasiru.usiru.mapper.domain.model.WaterAvailability
import com.hasiru.usiru.mapper.presentation.components.HasiruPrimaryButton
import com.hasiru.usiru.mapper.presentation.components.HasiruTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportPitScreen(lat: Double, lng: Double, onDone: () -> Unit, viewModel: ReportPitViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(lat, lng) {
        viewModel.initLocation(lat, lng)
        viewModel.loadRecommendations()
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { context.contentResolver.openInputStream(it)?.use { s -> viewModel.setImage(s.readBytes()) } }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.report_pit)) },
                navigationIcon = { IconButton(onClick = onDone) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = { launcher.launch("image/*") }) {
                Icon(Icons.Default.Photo, null); Text(stringResource(R.string.upload_photo))
            }
            Text(stringResource(R.string.soil_type))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                SoilType.entries.take(3).forEach { s ->
                    FilterChip(selected = state.soilType == s, onClick = { viewModel.updateSoil(s) }, label = { Text(s.name) })
                }
            }
            HasiruTextField(state.pitWidth, viewModel::updateWidth, stringResource(R.string.pit_width))
            HasiruTextField(state.pitDepth, viewModel::updateDepth, stringResource(R.string.pit_depth))
            HasiruTextField(state.conditions, viewModel::updateConditions, stringResource(R.string.nearby_conditions))
            if (state.recommendedSpecies.isNotEmpty()) {
                Card { Column(Modifier.padding(12.dp)) {
                    Text(stringResource(R.string.recommended_species), style = MaterialTheme.typography.titleMedium)
                    Text(state.recommendedSpecies.joinToString(", "))
                }}
            }
            HasiruPrimaryButton(stringResource(R.string.submit_pit), { viewModel.save(onDone) }, loading = state.isSaving)
        }
    }
}
