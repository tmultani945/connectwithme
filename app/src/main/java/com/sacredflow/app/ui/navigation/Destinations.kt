package com.sacredflow.app.ui.navigation

import kotlinx.serialization.Serializable

@Serializable object SplashRoute

// Onboarding nested graph
@Serializable object OnboardingGraph
@Serializable object OnboardingWelcomeRoute
@Serializable object OnboardingUseCaseRoute
@Serializable object OnboardingRecipientRoute
@Serializable object OnboardingNeedRoute
@Serializable object OnboardingToneRoute
@Serializable object OnboardingContextRoute

// Bottom-nav destinations
@Serializable object HomeRoute
@Serializable object LibraryRoute
@Serializable object ReminderRoute
@Serializable object SettingsRoute

// Full-screen destinations
@Serializable object CreateRoute
@Serializable object GenerationLoadingRoute
@Serializable object ResultRoute
@Serializable data class PrayerDetailRoute(val prayerId: Long)
@Serializable object PaywallRoute
@Serializable object HelpRoute
@Serializable object CrisisResourcesRoute

/**
 * Marker — used by the NavHost to decide whether to render the bottom nav.
 */
val bottomNavRouteSimpleNames: Set<String> = setOf(
    HomeRoute::class.simpleName!!,
    LibraryRoute::class.simpleName!!,
    ReminderRoute::class.simpleName!!,
    SettingsRoute::class.simpleName!!
)
