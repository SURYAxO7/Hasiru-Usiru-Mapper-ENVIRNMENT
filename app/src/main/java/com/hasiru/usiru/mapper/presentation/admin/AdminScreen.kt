package com.hasiru.usiru.mapper.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
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
import com.hasiru.usiru.mapper.domain.model.ReportStatus
import com.hasiru.usiru.mapper.domain.model.UserRole
import com.hasiru.usiru.mapper.domain.repository.AuthRepository
import com.hasiru.usiru.mapper.domain.repository.PitRepository
import com.hasiru.usiru.mapper.domain.repository.TreeRepository
import com.hasiru.usiru.mapper.presentation.components.StatCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminUiState(
    val isAdmin: Boolean = false,
    val totalTrees: Int = 0,
    val totalPits: Int = 0,
    val pendingTrees: Int = 0,
    val pendingPits: Int = 0
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val treeRepository: TreeRepository,
    private val pitRepository: PitRepository
) : ViewModel() {
    private val _state = MutableStateFlow(AdminUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val profile = authRepository.getProfile()
            val isAdmin = profile?.role == UserRole.ADMIN || profile?.role == UserRole.MUNICIPALITY
            combine(treeRepository.observeTrees(), pitRepository.observePits()) { trees, pits ->
                AdminUiState(
                    isAdmin = isAdmin,
                    totalTrees = trees.size,
                    totalPits = pits.size,
                    pendingTrees = trees.count { it.status == ReportStatus.PENDING },
                    pendingPits = pits.count { it.status == ReportStatus.PENDING }
                )
            }.collect { _state.value = it }
        }
    }

    fun approvePit(id: String) {
        viewModelScope.launch {
            pitRepository.updatePitStatus(id, ReportStatus.APPROVED, "Approved by admin")
        }
    }

    fun rejectPit(id: String) {
        viewModelScope.launch {
            pitRepository.updatePitStatus(id, ReportStatus.REJECTED, "Rejected by admin")
        }
    }
}

@Composable
fun AdminScreen(viewModel: AdminViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    if (!state.isAdmin) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text(stringResource(R.string.admin_access_denied))
        }
        return
    }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Icon(Icons.Default.AdminPanelSettings, null, modifier = Modifier.size(32.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.admin_dashboard), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(stringResource(R.string.total_trees), state.totalTrees.toString(), Modifier.weight(1f))
                StatCard(stringResource(R.string.total_pits), state.totalPits.toString(), Modifier.weight(1f))
            }
        }
        item {
            Card { Column(Modifier.padding(16.dp)) {
                Text(stringResource(R.string.pending_reviews), style = MaterialTheme.typography.titleMedium)
                Text("${stringResource(R.string.trees)}: ${state.pendingTrees}")
                Text("${stringResource(R.string.pits)}: ${state.pendingPits}")
            }}
        }
    }
}
