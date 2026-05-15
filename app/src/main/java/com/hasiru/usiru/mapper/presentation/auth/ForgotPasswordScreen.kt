package com.hasiru.usiru.mapper.presentation.auth

import androidx.compose.foundation.layout.*
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
fun ForgotPasswordScreen(onBack: () -> Unit, viewModel: AuthViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    var sent by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.forgot_password)) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(24.dp)) {
            if (sent) {
                Text(stringResource(R.string.reset_email_sent))
            } else {
                HasiruTextField(state.email, viewModel::updateEmail, stringResource(R.string.email))
                Spacer(Modifier.height(24.dp))
                HasiruPrimaryButton(
                    stringResource(R.string.send_reset_link),
                    { viewModel.resetPassword { sent = true } },
                    loading = state.isLoading
                )
            }
        }
    }
}
