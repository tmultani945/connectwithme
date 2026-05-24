package com.sacredflow.app.ui.navigation

import kotlinx.serialization.Serializable

@Serializable object SplashRoute

// Onboarding nested graph — synced with the 4-step Create flow:
//   Welcome → Recipient → Topic → Tone → Context (name + extra)
@Serializable object OnboardingGraph
@Serializable object OnboardingWelcomeRoute
@Serializable object OnboardingRecipientRoute
@Serializable object OnboardingTopicRoute
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
