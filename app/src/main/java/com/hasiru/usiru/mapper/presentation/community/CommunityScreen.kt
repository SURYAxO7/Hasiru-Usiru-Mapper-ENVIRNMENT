package com.hasiru.usiru.mapper.presentation.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasiru.usiru.mapper.R
import com.hasiru.usiru.mapper.domain.model.LeaderboardEntry
import com.hasiru.usiru.mapper.domain.repository.AuthRepository
import com.hasiru.usiru.mapper.domain.repository.DashboardRepository
import com.hasiru.usiru.mapper.presentation.theme.OxygenGold
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _leaderboard = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboard: StateFlow<List<LeaderboardEntry>> = _leaderboard.asStateFlow()

    init {
        viewModelScope.launch {
            val city = authRepository.getProfile()?.city ?: "Bengaluru"
            dashboardRepository.observeLeaderboard(city).collect { _leaderboard.value = it }
        }
    }
}

@Composable
fun CommunityScreen(viewModel: CommunityViewModel = hiltViewModel()) {
    val leaderboard by viewModel.leaderboard.collectAsState()

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(stringResource(R.string.leaderboard), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.community_subtitle), style = MaterialTheme.typography.bodyMedium)
        }
        item {
            Card {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EmojiEvents, null, tint = OxygenGold, modifier = Modifier.size(40.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(stringResource(R.string.weekly_challenge), style = MaterialTheme.typography.titleMedium)
                        Text(stringResource(R.string.challenge_desc))
                        Text(stringResource(R.string.challenge_reward), color = OxygenGold)
                    }
                }
            }
        }
        items(leaderboard) { entry ->
            ListItem(
                leadingContent = {
                    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(40.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("#${entry.rank}", fontWeight = FontWeight.Bold)
                        }
                    }
                },
                headlineContent = { Text(entry.userName) },
                supportingContent = { Text("${entry.treesTagged} trees • ${entry.points} pts") },
                trailingContent = { if (entry.rank <= 3) Icon(Icons.Default.EmojiEvents, null, tint = OxygenGold) }
            )
        }
        if (leaderboard.isEmpty()) {
            item { Text(stringResource(R.string.no_leaderboard_data)) }
        }
    }
}
