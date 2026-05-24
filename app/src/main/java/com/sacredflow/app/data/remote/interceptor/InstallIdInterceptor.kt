package com.sacredflow.app.data.remote.interceptor

import com.sacredflow.app.BuildConfig
import com.sacredflow.app.core.installid.InstallIdProvider
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class InstallIdInterceptor @Inject constructor(
    private val installIdProvider: InstallIdProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val installId = runBlocking { installIdProvider.get() }
        val request = chain.request().newBuilder()
            .addHeader("X-Install-Id", installId)
            .addHeader("X-App-Version", BuildConfig.VERSION_NAME)
            .addHeader("X-Platform", "android")
            .build()
        return chain.proceed(request)
    }
}
