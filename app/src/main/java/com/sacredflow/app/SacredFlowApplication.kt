package com.sacredflow.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.sacredflow.app.core.analytics.Analytics
import com.sacredflow.app.core.analytics.AnalyticsEvent
import com.sacredflow.app.core.init.AppInitializer
import com.sacredflow.app.data.repository.BillingRepository
import com.sacredflow.app.ui.screen.paywall.PaywallBillingHost
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SacredFlowApplication : Application(), Configuration.Provider, PaywallBillingHost {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var appInitializer: AppInitializer
    @Inject lateinit var analytics: Analytics
    @Inject lateinit var billingRepository: BillingRepository

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        createReminderNotificationChannel()
        appInitializer.initialize()
        analytics.log(AnalyticsEvent.AppOpened)
    }

    override fun billingRepository(): BillingRepository = billingRepository

    private fun createReminderNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                REMINDER_CHANNEL_ID,
                getString(R.string.reminder_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.reminder_channel_description)
                setShowBadge(false)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val REMINDER_CHANNEL_ID = "sacred_flow_daily_reminder"
    }
}
