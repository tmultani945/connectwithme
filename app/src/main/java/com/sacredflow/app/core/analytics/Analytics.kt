package com.sacredflow.app.core.analytics

import android.util.Log

interface Analytics {
    fun log(event: AnalyticsEvent)
}

/**
 * MVP default: log to logcat in debug, no-op in release.
 * Real analytics provider can be swapped in DI later.
 */
class LogcatAnalytics : Analytics {
    override fun log(event: AnalyticsEvent) {
        if (com.sacredflow.app.BuildConfig.DEBUG) {
            Log.d(TAG, "${event.name} ${event.params}")
        }
    }

    companion object {
        private const val TAG = "SacredFlow/Analytics"
    }
}
