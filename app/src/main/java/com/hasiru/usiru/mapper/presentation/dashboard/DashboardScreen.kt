package com.hasiru.usiru.mapper.presentation.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hasiru.usiru.mapper.R
import com.hasiru.usiru.mapper.presentation.components.ShimmerBox
import com.hasiru.usiru.mapper.presentation.components.StatCard
import com.hasiru.usiru.mapper.presentation.theme.OxygenGold
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = viewModel::refresh,
        modifier = Modifier.fillMaxSize()
    ) {
        if (state.isLoading) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(4) { ShimmerBox(Modifier.fillMaxWidth().height(100.dp)) }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        stringResource(R.string.dashboard_greeting),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text("${state.city} • ${stringResource(R.string.community_impact)}")
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard(
                            stringResource(R.string.trees_mapped),
                            state.stats.totalTrees.toString(),
                            Modifier.weight(1f)
                        ) { Icon(Icons.Default.Park, null, tint = MaterialTheme.colorScheme.onPrimary) }
                        StatCard(
                            stringResource(R.string.empty_pits),
                            state.stats.totalEmptyPits.toString(),
                            Modifier.weight(1f)
                        ) { Icon(Icons.Default.LocationOff, null, tint = MaterialTheme.colorScheme.onPrimary) }
                    }
                }
                item {
                    Card(shape = RoundedCornerShape(20.dp)) {
                        Row(Modifier.padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(stringResource(R.string.oxygen_score), style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "%.1f".format(state.stats.communityOxygenScore),
                                    style = MaterialTheme.typography.displayLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = OxygenGold
                                )
                            }
                            Icon(Icons.Default.Air, null, modifier = Modifier.size(48.dp), tint = OxygenGold)
                        }
                    }
                }
                item {
                    Text(stringResource(R.string.recent_activity), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                }
                items(state.stats.recentActivities) { activity ->
                    ListItem(
                        headlineContent = { Text(activity.title) },
                        supportingContent = { Text(activity.description) },
                        leadingContent = { Icon(Icons.Default.Eco, null) },
                        trailingContent = {
                            Text(SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(activity.timestamp)))
                        }
                    )
                }
            }
        }
    }
}
