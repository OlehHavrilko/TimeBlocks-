package com.timeblocks.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.timeblocks.presentation.viewmodel.AuthViewModel
import com.timeblocks.presentation.viewmodel.HomeViewModel
import com.timeblocks.presentation.screens.auth.LoginScreen
import com.timeblocks.presentation.screens.auth.SignUpScreen
import com.timeblocks.presentation.screens.home.HomeScreen
import com.timeblocks.presentation.screens.planner.PlannerScreen
import com.timeblocks.presentation.screens.statistics.StatisticsScreen
import com.timeblocks.presentation.screens.achievements.AchievementsScreen
import com.timeblocks.presentation.screens.profile.ProfileScreen
import com.timeblocks.presentation.screens.settings.SettingsScreen
import com.timeblocks.presentation.screens.paywall.PaywallScreen
import com.timeblocks.presentation.screens.splash.SplashScreen

/**
 * Навигация приложения TimeBlocks.
 */
@Composable
fun TimeBlocksNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        /**
         * Splash Screen - Проверка авторизации
         */
        composable(Screen.Splash.route) {
            SplashScreen(
                onAuthCheck = { isLoggedIn ->
                    if (isLoggedIn) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        /**
         * Auth Screens
         */
        composable(Screen.Login.route) {
            val viewModel: AuthViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.events.collect { event ->
                    when (event) {
                        is AuthEvent.LoginSuccess -> {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                        is AuthEvent.Error -> {
                            // UI handles error display
                        }
                    }
                }
            }

            LoginScreen(
                state = state,
                onLogin = viewModel::signInWithEmail,
                onGoogleLogin = viewModel::signInWithGoogle,
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                },
                onDismissError = viewModel::clearError
            )
        }

        composable(Screen.SignUp.route) {
            val viewModel: AuthViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.events.collect { event ->
                    when (event) {
                        is AuthEvent.RegistrationSuccess -> {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                        is AuthEvent.Error -> {
                            // UI handles error display
                        }
                    }
                }
            }

            SignUpScreen(
                state = state,
                onSignUp = viewModel::signUpWithEmail,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onDismissError = viewModel::clearError
            )
        }

        /**
         * Main Screens
         */
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.events.collect { event ->
                    when (event) {
                        is HomeEvent.NavigateToAuth -> {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                        is HomeEvent.Error -> {
                            // UI handles error display
                        }
                        else -> {
                            // Other events handled in UI
                        }
                    }
                }
            }

            HomeScreen(
                state = state,
                onCreateBlock = viewModel::createBlock,
                onDeleteBlock = viewModel::deleteBlock,
                onStartBlock = viewModel::startBlock,
                onUpdateDate = viewModel::updateDate,
                onNavigateToPlanner = { navController.navigate(Screen.Planner.route) },
                onNavigateToStatistics = { navController.navigate(Screen.Statistics.route) },
                onNavigateToAchievements = { navController.navigate(Screen.Achievements.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onSignOut = {
                    viewModel.signOut()
                }
            )
        }

        composable(Screen.Planner.route) {
            PlannerScreen(
                onBack = { navController.popBackStack() },
                onNavigateToCreateBlock = { 
                    // Could navigate to create block dialog/screen
                }
            )
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Achievements.route) {
            AchievementsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToPremium = { navController.navigate(Screen.Paywall.route) }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Paywall.route) {
            PaywallScreen(
                onBack = { navController.popBackStack() },
                onPremiumPurchased = { 
                    navController.popBackStack()
                }
            )
        }
    }
}

/**
 * Определение всех экранов приложения
 */
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Home : Screen("home")
    object Planner : Screen("planner")
    object Statistics : Screen("statistics")
    object Achievements : Screen("achievements")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Paywall : Screen("paywall")
}