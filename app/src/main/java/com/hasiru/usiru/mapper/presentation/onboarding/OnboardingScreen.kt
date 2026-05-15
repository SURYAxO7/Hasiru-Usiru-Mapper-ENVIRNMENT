package com.hasiru.usiru.mapper.presentation.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hasiru.usiru.mapper.R
import com.hasiru.usiru.mapper.presentation.components.HasiruPrimaryButton
import com.hasiru.usiru.mapper.presentation.splash.SplashViewModel
import com.hasiru.usiru.mapper.presentation.theme.LeafGreen
import kotlinx.coroutines.launch

data class OnboardingPage(val title: String, val desc: String, val icon: ImageVector)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onComplete: () -> Unit, splashVm: SplashViewModel = hiltViewModel()) {
    val pages = listOf(
        OnboardingPage(stringResource(R.string.onboard_title_1), stringResource(R.string.onboard_desc_1), Icons.Default.Map),
        OnboardingPage(stringResource(R.string.onboard_title_2), stringResource(R.string.onboard_desc_2), Icons.Default.Eco),
        OnboardingPage(stringResource(R.string.onboard_title_3), stringResource(R.string.onboard_desc_3), Icons.Default.Groups)
    )
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        HorizontalPager(pagerState, Modifier.weight(1f)) { page ->
            val data = pages[page]
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Icon(data.icon, null, tint = LeafGreen, modifier = Modifier.size(100.dp))
                Spacer(Modifier.height(32.dp))
                Text(data.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Spacer(Modifier.height(16.dp))
                Text(data.desc, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            repeat(pages.size) { i ->
                Box(
                    Modifier.padding(4.dp).size(8.dp).clip(CircleShape)
                        .background(if (pagerState.currentPage == i) LeafGreen else MaterialTheme.colorScheme.outline)
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        HasiruPrimaryButton(
            text = if (pagerState.currentPage == pages.lastIndex) stringResource(R.string.get_started)
            else stringResource(R.string.next),
            onClick = {
                if (pagerState.currentPage == pages.lastIndex) {
                    scope.launch {
                        splashVm.markOnboardingComplete()
                        onComplete()
                    }
                } else scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
            }
        )
    }
}
