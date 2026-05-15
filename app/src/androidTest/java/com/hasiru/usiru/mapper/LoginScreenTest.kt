package com.hasiru.usiru.mapper

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.hasiru.usiru.mapper.presentation.MainActivity
import com.hasiru.usiru.mapper.presentation.theme.HasiruUsiruTheme
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appLaunches() {
        composeRule.waitForIdle()
        // Splash navigates to onboarding or login - app should render without crash
    }
}
