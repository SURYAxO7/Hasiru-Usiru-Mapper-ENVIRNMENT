package com.hasiru.usiru.mapper.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.hasiru.usiru.mapper.presentation.navigation.HasiruNavHost
import com.hasiru.usiru.mapper.presentation.theme.HasiruUsiruTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HasiruUsiruTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HasiruNavHost()
                }
            }
        }
    }
}
