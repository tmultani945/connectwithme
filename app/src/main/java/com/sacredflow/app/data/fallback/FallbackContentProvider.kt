package com.sacredflow.app.data.fallback

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class FallbackContentProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {

    @Volatile private var cached: FallbackContent? = null
    private val mutex = Mutex()
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun get(useCase: String, tone: String): String {
        val content = load()

        content.byUseCase[useCase]?.get(tone)
            ?.takeIf { it.isNotEmpty() }
            ?.let { return it.random() }

        content.byUseCase[useCase]
            ?.values
            ?.flatten()
            ?.takeIf { it.isNotEmpty() }
            ?.let { return it.random() }

        return content.generic.random()
    }

    private suspend fun load(): FallbackContent {
        cached?.let { return it }
        return mutex.withLock {
            cached?.let { return@withLock it }
            val text = withContext(Dispatchers.IO) {
                context.assets.open(ASSET_PATH).bufferedReader().use { it.readText() }
            }
            val parsed = json.decodeFromString<FallbackContent>(text)
            cached = parsed
            parsed
        }
    }

    private fun <T> List<T>.random(): T = this[Random.nextInt(size)]

    companion object {
        private const val ASSET_PATH = "fallback_content.json"
    }
}

@Serializable
private data class FallbackContent(
    val version: String,
    val generic: List<String>,
    val byUseCase: Map<String, Map<String, List<String>>>
)
