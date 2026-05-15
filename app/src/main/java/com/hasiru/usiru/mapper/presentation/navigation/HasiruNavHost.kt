package com.hasiru.usiru.mapper.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hasiru.usiru.mapper.presentation.auth.ForgotPasswordScreen
import com.hasiru.usiru.mapper.presentation.auth.LoginScreen
import com.hasiru.usiru.mapper.presentation.auth.RegisterScreen
import com.hasiru.usiru.mapper.presentation.main.MainScreen
import com.hasiru.usiru.mapper.presentation.onboarding.OnboardingScreen
import com.hasiru.usiru.mapper.presentation.pit.ReportPitScreen
import com.hasiru.usiru.mapper.presentation.splash.SplashScreen
import com.hasiru.usiru.mapper.presentation.splash.SplashViewModel
import com.hasiru.usiru.mapper.presentation.tree.TagTreeScreen

@Composable
fun HasiruNavHost() {
    val navController = rememberNavController()
    val splashVm: SplashViewModel = hiltViewModel()
    val splashState by splashVm.state.collectAsState()

    NavHost(navController = navController, startDestination = NavRoutes.SPLASH) {
        composable(NavRoutes.SPLASH) {
            SplashScreen(
                state = splashState,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(NavRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(NavRoutes.ONBOARDING) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }
        composable(NavRoutes.LOGIN) {
            LoginScreen(
                onNavigateRegister = { navController.navigate(NavRoutes.REGISTER) },
                onNavigateForgot = { navController.navigate(NavRoutes.FORGOT_PASSWORD) },
                onLoginSuccess = {
                    navController.navigate(NavRoutes.MAIN) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(NavRoutes.REGISTER) {
            RegisterScreen(
                onBack = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate(NavRoutes.MAIN) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(NavRoutes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(onBack = { navController.popBackStack() })
        }
        composable(NavRoutes.MAIN) {
            MainScreen(
                onTagTree = { lat, lng -> navController.navigate(NavRoutes.tagTree(lat, lng)) },
                onReportPit = { lat, lng -> navController.navigate(NavRoutes.reportPit(lat, lng)) },
                onLogout = {
                    // When "direct authentication" is used, logout can simply go back to splash or stay in main
                    navController.navigate(NavRoutes.SPLASH) {
                        popUpTo(NavRoutes.MAIN) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = NavRoutes.TAG_TREE,
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType },
                navArgument("lng") { type = NavType.StringType }
            )
        ) { entry ->
            TagTreeScreen(
                lat = entry.arguments?.getString("lat")?.toDoubleOrNull() ?: 0.0,
                lng = entry.arguments?.getString("lng")?.toDoubleOrNull() ?: 0.0,
                onDone = { navController.popBackStack() }
            )
        }
        composable(
            route = NavRoutes.REPORT_PIT,
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType },
                navArgument("lng") { type = NavType.StringType }
            )
        ) { entry ->
            ReportPitScreen(
                lat = entry.arguments?.getString("lat")?.toDoubleOrNull() ?: 0.0,
                lng = entry.arguments?.getString("lng")?.toDoubleOrNull() ?: 0.0,
                onDone = { navController.popBackStack() }
            )
        }
    }
}
