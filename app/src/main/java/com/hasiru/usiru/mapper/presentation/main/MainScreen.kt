package com.hasiru.usiru.mapper.presentation.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.hasiru.usiru.mapper.R
import com.hasiru.usiru.mapper.presentation.auth.AuthViewModel
import com.hasiru.usiru.mapper.presentation.community.CommunityScreen
import com.hasiru.usiru.mapper.presentation.dashboard.DashboardScreen
import com.hasiru.usiru.mapper.presentation.map.MapScreen
import com.hasiru.usiru.mapper.presentation.profile.ProfileScreen
import com.hasiru.usiru.mapper.presentation.species.SpeciesGuideScreen
import com.hasiru.usiru.mapper.presentation.theme.LeafGreen

@Composable
fun MainScreen(
    onTagTree: (Double, Double) -> Unit,
    onReportPit: (Double, Double) -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.nav_dashboard),
        stringResource(R.string.nav_map),
        stringResource(R.string.nav_species),
        stringResource(R.string.nav_community),
        stringResource(R.string.nav_profile)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                when (index) {
                                    0 -> if (selectedTab == 0) Icons.Filled.Dashboard else Icons.Outlined.Dashboard
                                    1 -> if (selectedTab == 1) Icons.Filled.Map else Icons.Outlined.Map
                                    2 -> if (selectedTab == 2) Icons.Filled.MenuBook else Icons.Outlined.MenuBook
                                    3 -> if (selectedTab == 3) Icons.Filled.EmojiEvents else Icons.Outlined.EmojiEvents
                                    else -> if (selectedTab == 4) Icons.Filled.Person else Icons.Outlined.Person
                                },
                                contentDescription = title
                            )
                        },
                        label = { Text(title) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = LeafGreen, selectedTextColor = LeafGreen)
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedTab == 1) {
                // Map tab - FAB handled via map tap
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> DashboardScreen()
                1 -> MapScreen(onTagTree = onTagTree, onReportPit = onReportPit)
                2 -> SpeciesGuideScreen()
                3 -> CommunityScreen()
                4 -> ProfileScreen(onLogout = onLogout)
            }
        }
    }
}
