package com.hasiru.usiru.mapper.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasiru.usiru.mapper.R
import com.hasiru.usiru.mapper.core.locale.LocaleManager
import com.hasiru.usiru.mapper.domain.model.UserProfile
import com.hasiru.usiru.mapper.domain.repository.AuthRepository
import com.hasiru.usiru.mapper.presentation.auth.AuthViewModel
import com.hasiru.usiru.mapper.presentation.components.HasiruPrimaryButton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val localeManager: LocaleManager
) : ViewModel() {
    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile = _profile.asStateFlow()
    val language = localeManager.languageFlow

    init {
        viewModelScope.launch { _profile.value = authRepository.getProfile() }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch { localeManager.setLanguage(lang) }
    }
}

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    profileVm: ProfileViewModel = hiltViewModel(),
    authVm: AuthViewModel = hiltViewModel()
) {
    val profile by profileVm.profile.collectAsState()
    val language by profileVm.language.collectAsState(initial = LocaleManager.ENGLISH)

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(Icons.Default.AccountCircle, null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary)
        Text(profile?.name ?: "", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(profile?.email ?: "")
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            StatItem(stringResource(R.string.points), "${profile?.contributionPoints ?: 0}")
            StatItem(stringResource(R.string.trees), "${profile?.treesTagged ?: 0}")
            StatItem(stringResource(R.string.pits), "${profile?.pitsReported ?: 0}")
        }
        HorizontalDivider()
        Text(stringResource(R.string.language), style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = language == LocaleManager.ENGLISH,
                onClick = { profileVm.setLanguage(LocaleManager.ENGLISH) },
                label = { Text("English") }
            )
            FilterChip(
                selected = language == LocaleManager.KANNADA,
                onClick = { profileVm.setLanguage(LocaleManager.KANNADA) },
                label = { Text("ಕನ್ನಡ") }
            )
        }
        profile?.badges?.takeIf { it.isNotEmpty() }?.let { badges ->
            Text(stringResource(R.string.badges), style = MaterialTheme.typography.titleMedium)
            Text(badges.joinToString(", "))
        }
        Spacer(Modifier.weight(1f))
        HasiruPrimaryButton(stringResource(R.string.logout), { authVm.logout(onLogout) })
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}
