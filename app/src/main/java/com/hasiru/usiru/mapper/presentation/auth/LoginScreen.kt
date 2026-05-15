package com.hasiru.usiru.mapper.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hasiru.usiru.mapper.R
import com.hasiru.usiru.mapper.presentation.components.HasiruPrimaryButton
import com.hasiru.usiru.mapper.presentation.components.HasiruTextField
import com.hasiru.usiru.mapper.presentation.theme.LeafGreen

@Composable
fun LoginScreen(
    onNavigateRegister: () -> Unit,
    onNavigateForgot: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Eco, null, tint = LeafGreen, modifier = Modifier.size(72.dp))
        Spacer(Modifier.height(16.dp))
        Text(stringResource(R.string.welcome_back), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(32.dp))
        HasiruTextField(state.email, viewModel::updateEmail, stringResource(R.string.email))
        Spacer(Modifier.height(12.dp))
        HasiruTextField(state.password, viewModel::updatePassword, stringResource(R.string.password))
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp)) }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onNavigateForgot) { Text(stringResource(R.string.forgot_password)) }
        Spacer(Modifier.height(16.dp))
        HasiruPrimaryButton(stringResource(R.string.login), { viewModel.login(onLoginSuccess) }, loading = state.isLoading)
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onNavigateRegister) { Text(stringResource(R.string.create_account)) }
    }
}
