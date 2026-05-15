package com.hasiru.usiru.mapper.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hasiru.usiru.mapper.presentation.theme.LeafGreen
import com.hasiru.usiru.mapper.presentation.theme.MintGreen

@Composable
fun HasiruPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(52.dp),
        enabled = enabled && !loading,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = LeafGreen)
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
        } else Text(text)
    }
}

@Composable
fun HasiruTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = !isPassword
    )
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null
) {
    Card(
        modifier = modifier.animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .background(Brush.linearGradient(listOf(LeafGreen, MintGreen)))
                .padding(16.dp)
        ) {
            Column {
                icon?.invoke()
                Text(title, style = MaterialTheme.typography.labelLarge, color = Color.White.copy(0.9f))
                Text(value, style = MaterialTheme.typography.headlineMedium, color = Color.White)
            }
        }
    }
}

@Composable
fun ShimmerBox(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(12.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {}
}

@Composable
fun LoadingOverlay(visible: Boolean) {
    if (visible) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = LeafGreen)
        }
    }
}

@Composable
fun ErrorBanner(message: String, onDismiss: () -> Unit) {
  if (message.isNotBlank()) {
    Snackbar(
      action = { TextButton(onClick = onDismiss) { Text("OK") } },
      modifier = Modifier.padding(8.dp)
    ) { Text(message) }
  }
}
