package com.hasiru.usiru.mapper.presentation.map

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.hasiru.usiru.mapper.R
import com.hasiru.usiru.mapper.presentation.theme.LeafGreen
import com.hasiru.usiru.mapper.presentation.theme.PitRed

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onTagTree: (Double, Double) -> Unit,
    onReportPit: (Double, Double) -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val bengaluru = LatLng(12.9716, 77.5946)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bengaluru, 13f)
    }

    val permissions = rememberMultiplePermissionsState(
        listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    )
    LaunchedEffect(Unit) { if (!permissions.allPermissionsGranted) permissions.launchMultiplePermissionRequest() }

    LaunchedEffect(state.userLocation) {
        state.userLocation?.let {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 15f))
        }
    }

    var tappedLocation by remember { mutableStateOf<LatLng?>(null) }
    var showActionSheet by remember { mutableStateOf(false) }

    val mapProperties = MapProperties(
        isMyLocationEnabled = permissions.allPermissionsGranted,
        mapType = if (state.isSatellite) MapType.SATELLITE else MapType.NORMAL
    )

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = MapUiSettings(zoomControlsEnabled = true, myLocationButtonEnabled = true),
            onMapClick = { latLng ->
                tappedLocation = latLng
                showActionSheet = true
                viewModel.clearSelection()
            }
        ) {
            state.trees.forEach { tree ->
                val pos = LatLng(tree.latitude, tree.longitude)
                Marker(
                    state = MarkerState(pos),
                    title = tree.species,
                    snippet = "O₂ ${"%.1f".format(tree.oxygenScore)}",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                    onClick = {
                        viewModel.selectTree(tree)
                        true
                    }
                )
            }
            state.pits.forEach { pit ->
                Marker(
                    state = MarkerState(LatLng(pit.latitude, pit.longitude)),
                    title = stringResource(R.string.empty_pit),
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                    onClick = {
                        viewModel.selectPit(pit)
                        true
                    }
                )
            }
        }

        Row(
            Modifier.align(Alignment.TopEnd).padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = viewModel::toggleSatellite,
                containerColor = MaterialTheme.colorScheme.surface
            ) { Icon(Icons.Default.Layers, null) }
        }

        if (showActionSheet && tappedLocation != null) {
            ModalBottomSheet(onDismissRequest = { showActionSheet = false }) {
                Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.map_action_title), style = MaterialTheme.typography.titleLarge)
                    Button(
                        onClick = {
                            tappedLocation?.let { onTagTree(it.latitude, it.longitude) }
                            showActionSheet = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = LeafGreen)
                    ) { Icon(Icons.Default.Park, null); Spacer(Modifier.width(8.dp)); Text(stringResource(R.string.tag_tree)) }

                    Button(
                        onClick = {
                            tappedLocation?.let { onReportPit(it.latitude, it.longitude) }
                            showActionSheet = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PitRed)
                    ) { Icon(Icons.Default.LocationOff, null); Spacer(Modifier.width(8.dp)); Text(stringResource(R.string.report_pit)) }
                }
            }
        }

        state.selectedTree?.let { tree ->
            MarkerInfoCard(
                title = tree.species,
                subtitle = "${tree.speciesKn} • O₂ ${"%.1f".format(tree.oxygenScore)}",
                details = tree.environmentalBenefits,
                imageUrl = tree.imageUrl,
                onDismiss = { viewModel.clearSelection() },
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
            )
        }
        state.selectedPit?.let { pit ->
            MarkerInfoCard(
                title = stringResource(R.string.empty_pit),
                subtitle = pit.recommendedSpecies.joinToString(", "),
                details = pit.nearbyConditions,
                imageUrl = pit.imageUrl,
                onDismiss = { viewModel.clearSelection() },
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
            )
        }
    }
}

@Composable
fun MarkerInfoCard(
    title: String,
    subtitle: String,
    details: String,
    imageUrl: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(8.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.titleLarge)
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium)
                }
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null) }
            }
            if (details.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(details, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
