package com.hasiru.usiru.mapper.presentation.tree

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hasiru.usiru.mapper.R
import com.hasiru.usiru.mapper.domain.model.HealthCondition
import com.hasiru.usiru.mapper.presentation.components.HasiruPrimaryButton
import com.hasiru.usiru.mapper.presentation.components.HasiruTextField
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagTreeScreen(lat: Double, lng: Double, onDone: () -> Unit, viewModel: TagTreeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(lat, lng) { viewModel.initLocation(lat, lng) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { stream ->
                viewModel.setImage(stream.readBytes())
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tag_tree)) },
                navigationIcon = { IconButton(onClick = onDone) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("${stringResource(R.string.location)}: $lat, $lng")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { galleryLauncher.launch("image/*") }) {
                    Icon(Icons.Default.Photo, null); Spacer(Modifier.width(4.dp)); Text(stringResource(R.string.upload_photo))
                }
                if (state.imageBytes != null) {
                    OutlinedButton(onClick = viewModel::analyzeImage, enabled = !state.isAnalyzing) {
                        if (state.isAnalyzing) CircularProgressIndicator(Modifier.size(20.dp))
                        else { Icon(Icons.Default.AutoAwesome, null); Text(stringResource(R.string.ai_identify)) }
                    }
                }
            }
            state.aiResult?.let { ai ->
                Card { Column(Modifier.padding(12.dp)) {
                    Text("${stringResource(R.string.ai_result)}: ${ai.speciesName} (${(ai.confidence * 100).toInt()}%)")
                    Text(ai.kannadaDescription)
                    Text(ai.healthSuggestions, style = MaterialTheme.typography.bodySmall)
                }}
            }
            HasiruTextField(state.species, viewModel::updateSpecies, stringResource(R.string.species_name))
            HasiruTextField(state.girthCm, viewModel::updateGirth, stringResource(R.string.girth_cm))
            HasiruTextField(state.ageYears, viewModel::updateAge, stringResource(R.string.age_estimate))
            Text(stringResource(R.string.health_condition))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                HealthCondition.entries.forEach { h ->
                    FilterChip(
                        selected = state.health == h,
                        onClick = { viewModel.updateHealth(h) },
                        label = { Text(h.name) }
                    )
                }
            }
            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            HasiruPrimaryButton(stringResource(R.string.save_tree), { viewModel.save(onDone) }, loading = state.isSaving)
        }
    }
}
