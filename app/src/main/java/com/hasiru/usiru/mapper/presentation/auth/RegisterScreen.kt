package com.hasiru.usiru.mapper.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hasiru.usiru.mapper.R
import com.hasiru.usiru.mapper.presentation.components.HasiruPrimaryButton
import com.hasiru.usiru.mapper.presentation.components.HasiruTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onBack: () -> Unit, onSuccess: () -> Unit, viewModel: AuthViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.register)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(24.dp).verticalScroll(rememberScrollState())
        ) {
            HasiruTextField(state.name, viewModel::updateName, stringResource(R.string.full_name))
            Spacer(Modifier.height(12.dp))
            HasiruTextField(state.email, viewModel::updateEmail, stringResource(R.string.email))
            Spacer(Modifier.height(12.dp))
            HasiruTextField(state.phone, viewModel::updatePhone, stringResource(R.string.phone))
            Spacer(Modifier.height(12.dp))
            HasiruTextField(state.password, viewModel::updatePassword, stringResource(R.string.password))
            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Spacer(Modifier.height(24.dp))
            HasiruPrimaryButton(stringResource(R.string.register), { viewModel.register(onSuccess) }, loading = state.isLoading)
        }
    }
}
