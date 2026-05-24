package com.sacredflow.app.core.analytics

sealed class AnalyticsEvent(val name: String, val params: Map<String, Any?> = emptyMap()) {

    data object AppOpened : AnalyticsEvent("app_opened")

    data object OnboardingStarted : AnalyticsEvent("onboarding_started")
    data class OnboardingStepCompleted(val step: String) :
        AnalyticsEvent("onboarding_step_completed", mapOf("step" to step))
    data object OnboardingCompleted : AnalyticsEvent("onboarding_completed")

    data class GenerationRequested(
        val useCase: String,
        val tone: String,
        val length: String,
        val isRegeneration: Boolean
    ) : AnalyticsEvent("generation_requested", mapOf(
        "use_case" to useCase, "tone" to tone, "length" to length, "is_regeneration" to isRegeneration
    ))

    data class GenerationSucceeded(val latencyMs: Long, val modelName: String) :
        AnalyticsEvent("generation_succeeded", mapOf("latency_ms" to latencyMs, "model" to modelName))

    data class GenerationFailed(val reason: String) :
        AnalyticsEvent("generation_failed", mapOf("reason" to reason))

    data class SafetyBlocked(val category: String) :
        AnalyticsEvent("safety_blocked", mapOf("category" to category))

    data object PrayerSaved : AnalyticsEvent("prayer_saved")
    data object PrayerFavorited : AnalyticsEvent("prayer_favorited")
    data object PrayerShared : AnalyticsEvent("prayer_shared")
    data object PrayerDeleted : AnalyticsEvent("prayer_deleted")

    data object ReminderSet : AnalyticsEvent("reminder_set")
    data object ReminderFired : AnalyticsEvent("reminder_fired")

    data object PaywallShown : AnalyticsEvent("paywall_shown")
    data class PaywallPurchased(val plan: String) :
        AnalyticsEvent("paywall_purchased", mapOf("plan" to plan))
}
