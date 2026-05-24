package com.sacredflow.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.sacredflow.app.ui.components.SacredBottomBar
import com.sacredflow.app.ui.screen.create.CreateScreen
import com.sacredflow.app.ui.screen.generation.GenerationLoadingScreen
import com.sacredflow.app.ui.screen.generation.ResultScreen
import com.sacredflow.app.ui.screen.help.CrisisResourcesScreen
import com.sacredflow.app.ui.screen.help.HelpScreen
import com.sacredflow.app.ui.screen.home.HomeScreen
import com.sacredflow.app.ui.screen.library.LibraryScreen
import com.sacredflow.app.ui.screen.library.PrayerDetailScreen
import com.sacredflow.app.ui.screen.onboarding.OnboardingContextScreen
import com.sacredflow.app.ui.screen.onboarding.OnboardingRecipientScreen
import com.sacredflow.app.ui.screen.onboarding.OnboardingToneScreen
import com.sacredflow.app.ui.screen.onboarding.OnboardingTopicScreen
import com.sacredflow.app.ui.screen.onboarding.OnboardingWelcomeScreen
import com.sacredflow.app.ui.screen.paywall.PaywallScreen
import com.sacredflow.app.ui.screen.reminder.ReminderScreen
import com.sacredflow.app.ui.screen.settings.SettingsScreen
import com.sacredflow.app.ui.screen.splash.SplashScreen

@Composable
fun SacredFlowNavHost(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRouteName = backStackEntry?.destination?.route
        ?.substringBefore("/")
        ?.substringAfterLast(".")
    val showBottomBar = currentRouteName in bottomNavRouteSimpleNames

    Scaffold(
        // Transparent so the app-level SacredAppBackdrop shows through every screen.
        // Individual screens' Scaffolds also use containerColor = Color.Transparent.
        containerColor = Color.Transparent,
        bottomBar = {
            if (showBottomBar) {
                SacredBottomBar(
                    currentRoute = currentRouteName,
                    onSelect = { destination ->
                        navController.navigate(destination) {
                            popUpTo(HomeRoute) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = SplashRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<SplashRoute> {
                SplashScreen(
                    onNavigateToOnboarding = {
                        navController.navigate(OnboardingGraph) {
                            popUpTo(SplashRoute) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(HomeRoute) {
                            popUpTo(SplashRoute) { inclusive = true }
                        }
                    }
                )
            }

            navigation<OnboardingGraph>(startDestination = OnboardingWelcomeRoute) {
                composable<OnboardingWelcomeRoute> {
                    OnboardingWelcomeScreen(
                        onNext = { navController.navigate(OnboardingRecipientRoute) }
                    )
                }
                composable<OnboardingRecipientRoute> {
                    OnboardingRecipientScreen(
                        navController = navController,
                        onNext = { navController.navigate(OnboardingTopicRoute) },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable<OnboardingTopicRoute> {
                    OnboardingTopicScreen(
                        navController = navController,
                        onNext = { navController.navigate(OnboardingToneRoute) },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable<OnboardingToneRoute> {
                    OnboardingToneScreen(
                        navController = navController,
                        onNext = { navController.navigate(OnboardingContextRoute) },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable<OnboardingContextRoute> {
                    OnboardingContextScreen(
                        navController = navController,
                        onGenerate = {
                            navController.navigate(GenerationLoadingRoute) {
                                popUpTo(OnboardingGraph) { inclusive = true }
                            }
                        },
                        onBack = { navController.popBackStack() },
                        onCrisisResources = {
                            navController.navigate(CrisisResourcesRoute)
                        }
                    )
                }
            }

            composable<HomeRoute> {
                HomeScreen(
                    onCreate = { navController.navigate(CreateRoute) },
                    onOpenPrayer = { id -> navController.navigate(PrayerDetailRoute(id)) }
                )
            }

            composable<LibraryRoute> {
                LibraryScreen(
                    onOpenPrayer = { id -> navController.navigate(PrayerDetailRoute(id)) }
                )
            }

            composable<ReminderRoute> {
                ReminderScreen()
            }

            composable<SettingsRoute> {
                SettingsScreen(
                    onOpenPaywall = { navController.navigate(PaywallRoute) },
                    onOpenHelp = { navController.navigate(HelpRoute) }
                )
            }

            composable<CreateRoute> {
                CreateScreen(
                    onGenerate = { navController.navigate(GenerationLoadingRoute) },
                    onPaywall = { navController.navigate(PaywallRoute) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable<GenerationLoadingRoute> {
                GenerationLoadingScreen(
                    onResultReady = {
                        navController.navigate(ResultRoute) {
                            popUpTo(GenerationLoadingRoute) { inclusive = true }
                        }
                    },
                    onCrisisResources = {
                        navController.navigate(CrisisResourcesRoute) {
                            popUpTo(GenerationLoadingRoute) { inclusive = true }
                        }
                    },
                    onPaywall = {
                        navController.navigate(PaywallRoute) {
                            popUpTo(GenerationLoadingRoute) { inclusive = true }
                        }
                    }
                )
            }

            composable<ResultRoute> {
                ResultScreen(
                    onDone = {
                        // Clear back stack — works whether we arrived from onboarding
                        // (stack: Splash → Result) or from Home (stack: Splash → Home → Create → Loading → Result).
                        navController.navigate(HomeRoute) {
                            popUpTo(SplashRoute) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onOpenSaved = { id ->
                        // Same pattern — ensure Home is the back-target for the detail screen.
                        navController.navigate(HomeRoute) {
                            popUpTo(SplashRoute) { inclusive = true }
                            launchSingleTop = true
                        }
                        navController.navigate(PrayerDetailRoute(id))
                    }
                )
            }

            composable<PrayerDetailRoute> { entry ->
                val route: PrayerDetailRoute = entry.toRoute()
                PrayerDetailScreen(
                    prayerId = route.prayerId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable<PaywallRoute> {
                PaywallScreen(onClose = { navController.popBackStack() })
            }

            composable<HelpRoute> {
                HelpScreen(
                    onBack = { navController.popBackStack() },
                    onCrisisResources = { navController.navigate(CrisisResourcesRoute) }
                )
            }

            composable<CrisisResourcesRoute> {
                CrisisResourcesScreen(
                    onBack = {
                        navController.navigate(HomeRoute) {
                            popUpTo(SplashRoute) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}
